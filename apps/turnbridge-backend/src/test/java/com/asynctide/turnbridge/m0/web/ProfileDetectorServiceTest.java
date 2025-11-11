/**
 * 
 */
package com.asynctide.turnbridge.m0.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.asynctide.turnbridge.support.ProfileDetectorService;

/**
 * 
 */
public class ProfileDetectorServiceTest {
    @Test
    void detectCanonical() {
        var s = new ProfileDetectorService();
        assertEquals("Profile-Canonical", s.detectByHeader(Set.of("invoiceNo","buyerId","amount")));
    }
    @Test
    void detectLegacy() {
        var s = new ProfileDetectorService();
        assertEquals("Profile-Legacy", s.detectByHeader(Set.of("inv_no","buyer_id","amt")));
    }
}

