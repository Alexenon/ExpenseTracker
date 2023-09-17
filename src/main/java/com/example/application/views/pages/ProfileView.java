package com.example.application.views.pages;

import com.example.application.entities.User;
import com.example.application.services.UserService;
import com.example.application.views.pages.RegistrationView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@PermitAll
//@PageTitle("Profile")
//@Route(value = "profile", layout = MainLayout.class)
public class ProfileView extends Main {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationView.class);

    private final UserService userService;

    private final Binder<User> binder;

    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final PasswordField confirmPassword = new PasswordField("Confirm password");
    private final EmailField email = new EmailField("Email");

    public ProfileView(UserService userService) {
        this.userService = userService;
        binder = new Binder<>(User.class);
        initBinder();
        initContent();
        addStyle();
    }

    public void initContent() {
        logger.info("Accessed registration page");
        addClassName("page-content");

        H2 title = new H2("Profile");
        Button saveButton = new Button("Register", e -> {
            if (binder.validate().isOk()) {
                logger.info("Profile updated succesfully");
            } else {
                logger.warn("Submitted Profile Form with validation errors");
            }
        });

        add(
                title,
//                getTabSheet(),
                getTabs(),
                saveButton
        );
    }

    private TabSheet getTabSheet() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.add("Dashboard", new Div(new Text("This is the Dashboard tab content")));
        tabSheet.add("Payment", new Div(new Text("This is the Payment tab content")));

        return tabSheet;
    }

    private Tabs getTabs() {
        Tab profile = new Tab(VaadinIcon.USER.create(), new Span("Profile"));
        Tab settings = new Tab(VaadinIcon.COG.create(), new Span("Settings"));
        Tab notifications = new Tab(VaadinIcon.BELL.create(), new Span("Notifications"));

        profile.add(new Div(new Text("This is the Profile tab content")));
        settings.add(new Div(new Text("This is the Settings tab content")));
        notifications.add(new Div(new Text("This is the Notifications tab content")));

        Tabs tabs = new Tabs(profile, settings, notifications);
        tabs.setSelectedTab(profile);
        tabs.setHeight("240px");
        tabs.setWidth("240px");
        tabs.setOrientation(Tabs.Orientation.VERTICAL);

        return tabs;
    }

    private void addStyle() {
        username.setEnabled(false);
        password.setEnabled(false);
        email.setEnabled(false);
    }

    private HorizontalLayout editLayout(AbstractField<?, ?> element) {
        HorizontalLayout layout = new HorizontalLayout(element, editButton(element));
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        return layout;
    }

    private Icon editButton(AbstractField<?, ?> element) {
        Icon icon = new Icon("lumo", "edit");
        icon.addClickListener(e -> {
            element.setEnabled(!element.isEnabled());
            setIconImage(icon, element.isEnabled() ? "cross" : "edit");
        });

        return icon;
    }

    private void setIconImage(Icon icon, String iconSrc) {
        icon.getElement().setAttribute("icon", "lumo:" + iconSrc);
    }

    private void initBinder() {
        binder.setBean(new User());
        binder.forField(username)
                .asRequired("Please fill this field")
                .withValidator(s -> s.length() > 3, "Username must contain at least 4 characters")
                .withValidator(s -> s.length() < 12, "Username must contain less than 12 characters")
                .withValidator(s -> userService.findByUsernameIgnoreCase(s).isEmpty(), "Username already exists")
                .bind(User::getUsername, User::setUsername);

        binder.forField(password)
                .asRequired("Please fill this field")
                .withValidator(t -> t.length() > 3, "Password must contain at least 4 characters")
                .withValidator(s -> s.length() < 12, "Password must contain less than 12 characters");

        binder.forField(confirmPassword)
                .asRequired("Please fill this field")
                .withValidator(s -> s.length() > 3, "Password must contain at least 4 characters")
                .withValidator(s -> s.equals(password.getValue()), "Passwords don't match")
                .bind(User::getPassword, User::setPassword);

        binder.forField(email)
                .asRequired("Please fill this field")
                .withValidator(new EmailValidator("Please enter a valid email address"))
                .withValidator(s -> userService.findByEmailIgnoreCase(s).isEmpty(), "This email is already used")
                .bind(User::getEmail, User::setEmail);
    }

}
