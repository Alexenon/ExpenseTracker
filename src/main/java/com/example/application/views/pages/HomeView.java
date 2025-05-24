package com.example.application.views.pages;

import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.core.ComponentBuilder;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.example.application.views.components.utils.HasScroll;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@AnonymousAllowed
@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
public class HomeView extends AbstractPage implements HasScroll {

    @Autowired
    private InstrumentsFacadeService service;

    public HomeView() {
        add(
                mainSection(),
                ourFeaturesSection(),
                ourUsersLikeSection(),
                footer()
        );
    }

    private Div mainSection() {
        return Container.builder("heading")
                .addComponent(() -> {
                    H1 h1 = new H1();
                    h1.add(new Span("Simple way"));
                    return h1;
                })
                .addComponent(() -> {
                    Container subheading = new Container("subheading");
                    subheading.add(new Paragraph("to manage"));
                    subheading.add(new Span("personal finances"));
                    return subheading;
                })
                .addComponent(() -> {
                    NativeButton button = new NativeButton("Register for free");
                    button.addClassName("download-btn");
                    return button;
                })
                .build();
    }

    private Div ourFeaturesSection() {
        return Container.builder()
                .addClassNames("features", "centered-text")
                .addComponent(() -> featureBox("Secured data", PictogramIcon.SHIELD_LOCK.create()))
                .addComponent(() -> featureBox("Innovative finance tools", PictogramIcon.BITCOIN.create()))
                .addComponent(() -> featureBox("Customizable experience", PictogramIcon.BRIEFCASE_EDIT.create()))
                .addComponent(() -> featureBox("Built by passionate founders", PictogramIcon.GITHUB.create()))
                .build();
    }

    private Div ourUsersLikeSection() {
        Container sectionHeader = Container.builder()
                .addClassNames("features-section-title", "centered-text")
                .addComponent(new H3("Features our users love"))
                .build();

        Container sectionBody = Container.builder("user-features")
                .addComponent(() -> userFeature("Multiple devices",
                        "Safely synchronize across devices with Bank standard security",
                        PictogramIcon.TABLET_CELLPHONE.create()
                ))
                .addComponent(() -> userFeature("Recurring transaction",
                        "Get notified of recurring bills and transactions before due date",
                        PictogramIcon.TRANSFER.create()
                ))
                .addComponent(() -> userFeature("Travel mode",          // TODO:::::::::::::::::::::::::::::::::::
                        "All currencies supported with up-to-date exchange rate",
                        PictogramIcon.ACCOUNT.create()
                ))
                .addComponent(() -> userFeature("Debt and loan",
                        "Manage your debts, loans and payment process in one place",
                        PictogramIcon.CASH_LOCK.create()
                ))
                .addComponent(() -> userFeature("Saving plan",
                        "Keep track on savings process to meet your financial goals",
                        PictogramIcon.PIGGY_BANK_OUTLINE.create()
                ))
                .addComponent(() -> userFeature("Effortless transaction entry",
                        "Entry a transaction quickly and easily, manually or automatically",
                        PictogramIcon.INVOICE_CLOCK_OUTLINE.create()
                ))
                .build();

        return Container.builder("our-users-like-section")
                .addComponent(sectionHeader)
                .addComponent(sectionBody)
                .build();
    }

    private Div userFeature(String name, String details, MonoIcon icon) {
        return Container.builder("user-feature")
                .addComponent(icon)
                .addComponent(new H3(name))
                .addComponent(() -> new ComponentBuilder<>(Paragraph.class)
                        .setText(details)
                        .addClass("centered-text")
                        .build())
                .build();
    }

    private Div featureBox(String name, MonoIcon icon) {
        return Container.builder("feature-box")
                .addComponent(icon)
                .addComponent(new Paragraph(name))
                .build();
    }

    private Html footer() {
        return new Html("""
                <footer class="footer">
                  <div class="footer-left">
                    <p>© 2025 Finsify .,JSC. All rights reserved.</p>
                    <nav class="footer-links">
                      <a href="#">About us</a>
                      <a href="#">Career</a>
                      <a href="#">Blog</a>
                      <a href="#">Status</a>
                      <a href="#">Privacy Policy</a>
                      <a href="#">Terms of Service</a>
                    </nav>
                  </div>
                
                  <div class="footer-right">
                    <div class="social-icons">
                      <a href="#"><img src="https://img.icons8.com/ios-glyphs/30/000000/facebook-new.png" alt="Facebook" /></a>
                      <a href="#"><img src="https://img.icons8.com/ios-glyphs/30/000000/instagram-new.png" alt="Instagram" /></a>
                      <a href="#"><img src="https://img.icons8.com/ios-glyphs/30/000000/twitter--v1.png" alt="Twitter" /></a>
                    </div>
                    </div>
                </footer>
                """);
    }

}




