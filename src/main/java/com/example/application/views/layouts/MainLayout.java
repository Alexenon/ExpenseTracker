package com.example.application.views.layouts;

import com.example.application.services.SecurityService;
import com.example.application.views.components.complex_components.NavigationBar;
import com.example.application.views.components.complex_components.icons.MonoIcon;
import com.example.application.views.components.complex_components.icons.PictogramIcon;
import com.example.application.views.pages.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.shared.Tooltip;
import org.springframework.beans.factory.annotation.Autowired;

public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    private final NavigationBar navigationBar = new NavigationBar();
    private final Header header = new Header(navigationBar);

    private final MonoIcon loginIcon = new MonoIcon(PictogramIcon.LOGIN);
    private final MonoIcon logoutIcon = new MonoIcon(PictogramIcon.LOGOUT);
    private final MonoIcon accountIcon = new MonoIcon(PictogramIcon.ACCOUNT);

    @Autowired
    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        build();
    }

    private void build() {
        header.setId("header");
        accountIcon.addClassName("user-icon");

        initialize();
        buildNavigationBar();
    }

    private void buildNavigationBar() {
        boolean isLoggedIn = securityService.getAuthenticatedUserDetails().isPresent();

        if (isLoggedIn) {
            loginIcon.removeFromParent();
            navigationBar.add(accountIcon, logoutIcon);
        } else {
            accountIcon.removeFromParent();
            logoutIcon.removeFromParent();
            navigationBar.add(loginIcon);
        }

        navigationBar.getTextLinks().forEach(c -> c.setVisible(isLoggedIn));
    }

    private void initialize() {
        Tooltip accountIconTooltip = Tooltip.forComponent(accountIcon);
        accountIconTooltip.setText("Account");

        Tooltip loginIconTooltip = Tooltip.forComponent(loginIcon);
        loginIconTooltip.setText("Login");

        Tooltip logoutIconTooltip = Tooltip.forComponent(logoutIcon);
        logoutIconTooltip.setText("Logout");

        accountIconTooltip.setPosition(Tooltip.TooltipPosition.BOTTOM);
        loginIconTooltip.setPosition(Tooltip.TooltipPosition.BOTTOM);
        logoutIconTooltip.setPosition(Tooltip.TooltipPosition.BOTTOM);

        loginIcon.addClickListener(e -> {
            UI.getCurrent().navigate(LoginView.class);
            buildNavigationBar();
        });

        logoutIcon.addClickListener(e -> {
            securityService.logout();
            buildNavigationBar();
        });

        addToNavbar(header);
    }

}
