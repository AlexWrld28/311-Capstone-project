package org.csc311.capstone.models;

/**
 * @author Charles Gonzalez Jr
 * @param email
 * @param password
 * @param firstName
 * @param lastName
 * @param department
 */
public record RegisterDTO(String email,String password,String firstName,String lastName,String department) {
}
