package com.auth.ekyc.ekyc_backend.domain.enums;

public enum KycStatus {

    // user just started but not submitted
    DRAFT,

    // user pressed submit, waiting pre-check
    SUBMITTED,

    // sent to provider AI / OCR / face match
    PROCESSING,

    // provider cannot decide, human needed
    MANUAL_REVIEW,

    // approved successfully
    APPROVED,

    // rejected permanently
    REJECTED,

    // user must fix & resubmit
    RETRY_REQUIRED,

    // link expired / timeout
    EXPIRED
}
