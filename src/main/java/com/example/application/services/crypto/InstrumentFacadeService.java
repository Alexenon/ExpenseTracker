package com.example.application.services.crypto;

import com.example.application.services.SecurityService;
import org.springframework.stereotype.Service;

/**
 * Service that provides information just for authenticated user and guest user
 * and hides other information that user is not supposed to have
 * */
@Service
public class InstrumentFacadeService {

    private final SecurityService securityService;
    private final InstrumentsService instrumentsService;

    public InstrumentFacadeService(SecurityService securityService,
                                   InstrumentsService instrumentsService) {
        this.securityService = securityService;
        this.instrumentsService = instrumentsService;
    }
}
