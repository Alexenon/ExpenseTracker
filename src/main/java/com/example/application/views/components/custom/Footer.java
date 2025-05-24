package com.example.application.views.components.custom;

import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.example.application.views.pages.ExpensesView;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.RouterLink;

public class Footer extends com.vaadin.flow.component.html.Footer {

    private final RouterLink termsLink = new RouterLink("Terms of Service", ExpensesView.class);
    private final RouterLink policyLink = new RouterLink("Privacy Policy", ExpensesView.class);
    private final MonoIcon githubIcon = PictogramIcon.GITHUB.create();
    private final MonoIcon facebookIcon = PictogramIcon.FACEBOOK.create();
    private final MonoIcon twitterIcon = PictogramIcon.TWITTER.create();

    public Footer() {
        addClassName("footer");
        add(
                copywriteArea(),
                plainLinkArea(),
                iconsLinkArea()
        );
    }

    private Container copywriteArea() {
        return Container.builder("footer-copywrite", "centered-row")
                .addComponent(new Span("©"))
                .addComponent(new Paragraph("All rights reserved."))
                .build();
    }

    private Container plainLinkArea() {
        return Container.builder("footer-links", "centered-row")
                .addComponents(termsLink, policyLink)
                .build();
    }

    private Container iconsLinkArea() {
        return Container.builder("social-icons")
                .addComponents(githubIcon, facebookIcon, twitterIcon)
                .build();
    }

}
