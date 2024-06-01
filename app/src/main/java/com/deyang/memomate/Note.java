package com.deyang.memomate;

import java.util.Objects;

/**
 * A class representing a note in the note-taking application.
 */
public class Note {

    private String title;
    private String content;
    private boolean isPinned;

    /**
     * Default constructor for the Note class.
     * Initializes an empty note.
     */
    public Note(){}

    /**
     * Constructor for creating a Note object with specified title and content.
     *
     * @param title The title of the note.
     * @param content The content of the note.
     */
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }



    /**
     * Getter for the title of the note.
     *
     * @return The title of the note.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the title of the note.
     *
     * @param title The new title for the note.
     */
    public void setTitle(String title){
        this.title = title;
    }

    /**
     * Getter for the content of the note.
     *
     * @return The content of the note.
     */
    public String getContent(){
        return content;
    }

    /**
     * Setter for the content of the note.
     *
     * @param content The new content for the note.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Getter for the pin status of the note.
     *
     * @return True if the note is pinned, false otherwise.
     */
    public boolean isPinned() {
        return isPinned;
    }

    /**
     * Setter for the pin status of the note.
     *
     * @param isPinned The new pin status for the note.
     */
    public void setIsPinned(boolean isPinned) {
        this.isPinned = isPinned;
    }

    /**
     * Determines whether this {@code Note} object is equal to another object.
     * Two {@code Note} objects are considered equal if both their titles and contents match exactly.
     *<p>
     * This method checks for equality based on the {@code title} and {@code content} fields only.
     * It does not consider the {@code id} or {@code isPinned} fields in determining equality.
     *
     * @param obj The object to compare against this {@code Note}.
     * @return {@code true} if the given object represents a {@code Note} that is equal to this one,
     *  {@code false} otherwise.
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass()!= obj.getClass()) {
            return false;
        }
        Note otherNote = (Note) obj;
        return Objects.equals(title, otherNote.title) && Objects.equals(content, otherNote.content);
    }

    /**
     * Computes a hash code for this {@code Note} object, based on its {@code title} and {@code content}.
     * <p>
     * This implementation of {@code hashCode} is consistent with {@link Object#equals(Object)}:
     * if two {@code Note} objects are equal according to the {@link #equals(Object)} method, then
     * calling the {@code hashCode} method on each of the two objects must produce the same integer result.
     * <p>
     *     It is recommended to override both {@link #equals(Object)} and {@code hashCode} methods when you
     * override one of them.
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, content);
    }
}
