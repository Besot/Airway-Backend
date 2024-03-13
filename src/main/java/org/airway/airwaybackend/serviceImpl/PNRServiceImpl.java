package org.airway.airwaybackend.serviceImpl;

import org.airway.airwaybackend.repository.PNRRepository;
import org.springframework.stereotype.Service;

@Service
public class PNRServiceImpl {
private final PNRRepository pnrRepository;

    public PNRServiceImpl(PNRRepository pnrRepository) {
        this.pnrRepository = pnrRepository;
    }


}
