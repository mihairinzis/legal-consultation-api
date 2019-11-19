package com.code4ro.legalconsultation.common.exceptions;

public class ConflictingMetadataException extends RuntimeException {
    public ConflictingMetadataException(){ super("Conflicting details provided"); }
}
