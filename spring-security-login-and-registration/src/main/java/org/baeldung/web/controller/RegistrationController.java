package org.baeldung.web.controller;

import java.util.Calendar;
import javax.validation.Valid;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.VerificationToken;
import org.baeldung.persistence.service.UserDto;
import org.baeldung.persistence.service.IUserService;
import org.baeldung.event.OnRegistrationCompleteEvent;
import org.baeldung.validation.service.EmailExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RegistrationController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private IUserService service;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public RegistrationController(IUserService service) {
        this.service = service;
    }

    @RequestMapping(value = "/user/registration", method = RequestMethod.GET)
    public String showRegistrationForm(WebRequest request, Model model) {
        LOGGER.debug("Rendering registration page.");
        UserDto accountDto = new UserDto();
        model.addAttribute("user", accountDto);
        return "registration";
    }

    @RequestMapping(value = "/regitrationConfirm", method = RequestMethod.GET)
    public String confirmRegistration(WebRequest request, Model model, @RequestParam("token") String token) {
        VerificationToken verificationToken = service.getVerificationToken(token);
        if (verificationToken == null) {
            model.addAttribute("message", messages.getMessage("auth.message.invalidToken", null, request.getLocale()));
            return "redirect:/badUser.html?lang=" + request.getLocale().getLanguage();

        }
        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if (user == null) {
            model.addAttribute("message", messages.getMessage("auth.message.invalidUser", null, request.getLocale()));
            return "redirect:/badUser.html?lang=" + request.getLocale().getLanguage();
        }
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            user.setEnabled(false);
        } else {
            user.setEnabled(true);
        }
        service.saveRegisteredUser(user);
        return "redirect:/login.html?lang=" + request.getLocale().getLanguage();
    }

    @RequestMapping(value = "/user/registration", method = RequestMethod.POST)
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDto accountDto, BindingResult result, WebRequest request, Errors errors) {
        LOGGER.debug("Registering user account with information: {}", accountDto);
        User registered = new User();
        String appUrl = request.getContextPath();
        if (result.hasErrors()) {
            return new ModelAndView("registration", "user", accountDto);
        }
        registered = createUserAccount(accountDto);
        if (registered == null) {
            result.rejectValue("email", "message.regError");
        }
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
        return new ModelAndView("successRegister", "user", accountDto);
    }

    private User createUserAccount(UserDto accountDto) {
        User registered = null;
        try {
            registered = service.registerNewUserAccount(accountDto);
        } catch (EmailExistsException e) {
            return null;
        }
        return registered;
    }
}
