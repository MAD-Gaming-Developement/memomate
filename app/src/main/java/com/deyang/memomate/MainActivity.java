package com.deyang.memomate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The main activity of the Memo Note.
 */
public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "NotePrefs";
    private static final String KEY_NOTE_COUNT = "NoteCount";
    private LinearLayout notesContainer;
    private List<Note> noteList;
    private List<Note> filteredNoteList;
    private SearchView searchView;


    /**
     * Called when the activity is starting. This is where most initialization
     * should go: calling SetContentView(int) to inflate the activity's UI,
     * findViewByID(int) to retrieve references to UI widgets, and initializing
     * variables.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesContainer = findViewById(R.id.notesContainer);
        searchView = findViewById(R.id.searchView);
        Button saveButton = findViewById(R.id.saveButton);

        noteList = new ArrayList<>();
        filteredNoteList = new ArrayList<>();

        saveButton.setOnClickListener(view -> {
            saveNote();
            displayNotes(noteList);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterNotes(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterNotes(newText);
                return false;
            }
        });

        loadNotesFromPreferences();
        displayNotes(noteList);
    }

    /**
     * Displays the list of notes.
     * @param notes The list of notes to be displayed.
     */
    private void displayNotes(List<Note> notes) {
        System.out.println("Displaying notes. Total notes: " + notes.size());
        sortNotesByPinStatus(notes);
        notesContainer.removeAllViews();
        for (Note note : notes) {
            createNoteView(note);
        }
    }


    /**
     * Sorts the given list of {@link Note} objects based on their pin status,
     * ensuring that pinned notes appear above unpinned ones.
     * <p>
     * This method reorders the entire list so that all pinned notes precede
     * unpinned notes. If two notes have the same pin status, their original
     * order in the list is preserved.
     *
     * @param notes The list of {@code Note} objects to be sorted.
     */
    private void sortNotesByPinStatus(List<Note> notes) {
        Collections.sort(notes, (n1, n2) -> {
            if (n1.isPinned() &&!n2.isPinned()) {
                return -1;
            } else if (!n1.isPinned() && n2.isPinned()) {
                return 1;
            } else {
                return 0;
            }
        });
    }



    /**
     * Loads notes from SharedPreferences.
     */
    private void loadNotesFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int noteCount = sharedPreferences.getInt(KEY_NOTE_COUNT, 0);

        for (int i = 0; i < noteCount; i++) {
            String title = sharedPreferences.getString("note_title_" + i, "");
            String content = sharedPreferences.getString("note_content_" + i, "");

            Note note = new Note();
            note.setTitle(title);
            note.setContent(content);

            noteList.add(note);
        }
        filteredNoteList.addAll(noteList);
    }

    /**
     * Saves a new Note
     */
    private void saveNote() {
        EditText titleEditText = findViewById(R.id.titleEditText);
        EditText contentEditText = findViewById(R.id.contentEditText);

        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        if (!title.isEmpty() && !content.isEmpty()) {
            Note note = new Note();
            note.setTitle(title);
            note.setContent(content);

            noteList.add(note);
            saveNotesToPreferences();

            filterNotes(searchView.getQuery().toString());
            clearInputFields();
        }
    }

    /**
     * Clears input fields after saving a note.
     */
    private void clearInputFields() {
        EditText titleEditText = findViewById(R.id.titleEditText);
        EditText contentEditText = findViewById(R.id.contentEditText);

        titleEditText.getText().clear();
        contentEditText.getText().clear();
    }

    /**
     * Creates a view for displaying a note.
     *
     * @param note The note to be displayed.
     */
    private void createNoteView(final Note note) {
        View noteView = getLayoutInflater().inflate(R.layout.note_item, null);
        TextView titleTextView = noteView.findViewById(R.id.TitleTextView);
        TextView contentTextView = noteView.findViewById(R.id.contentTextView);
        Button editButton = noteView.findViewById(R.id.editButton);
        Button deleteButton = noteView.findViewById(R.id.deleteButton);
        Button pinButton = noteView.findViewById(R.id.pinButton);

        titleTextView.setText(note.getTitle());
        contentTextView.setText(note.getContent());


        pinButton.setText(note.isPinned()? "Unpin" : "Pin");

        pinButton.setOnClickListener(v -> {
            note.setIsPinned(!note.isPinned());
            pinButton.setText(note.isPinned()? "Unpin" : "Pin");
            saveNotesToPreferences();
            displayNotes(noteList);
        });

        editButton.setOnClickListener(v -> showEditDialog(note));

        deleteButton.setOnClickListener(v -> showDeleteDialog(note));

        notesContainer.addView(noteView);
    }


    /**
     * Displays a dialog for editing a note.
     *
     * @param note The note to be edited.
     */
    private void showEditDialog(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Note");

        View viewInflated = getLayoutInflater().inflate(R.layout.dialog_edit_note, null);
        final EditText titleEditText = viewInflated.findViewById(R.id.titleEditText);
        final EditText contentEditText = viewInflated.findViewById(R.id.contentEditText);

        titleEditText.setText(note.getTitle());
        contentEditText.setText(note.getContent());

        builder.setView(viewInflated);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = titleEditText.getText().toString();
            String newContent = contentEditText.getText().toString();
            if (!newTitle.isEmpty() && !newContent.isEmpty()) {
                note.setTitle(newTitle);
                note.setContent(newContent);
                saveNotesToPreferences();
                filterNotes(searchView.getQuery().toString());
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Displays a dialog for deleting a note.
     *
     * @param note The note to be deleted.
     */
    private void showDeleteDialog(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this note");
        builder.setMessage("Are you sure you want to delete this note?");
        builder.setPositiveButton("Delete", (dialogInterface, i) -> deleteNoteAndRefresh(note));
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    /**
     * Deletes a note and refreshes the UI.
     *
     * @param note The note to be deleted.
     */
    private void deleteNoteAndRefresh(Note note) {
        Log.d("DeleteNote", "Deleting note: " + note.getTitle());
        try {
            noteList.remove(note);
            saveNotesToPreferences();
            filterNotes(searchView.getQuery().toString());
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e("DeleteNote", "Error deleting note: " + e.getMessage());
        }
    }

    /**
     * Filters the list of notes based on the given query and displays the filtered notes.
     *
     * @param query The query string to filter notes.
     */
    private void filterNotes(String query) {
        filteredNoteList.clear();
        for (Note note : noteList) {
            if (note.getTitle().toLowerCase().contains(query.toLowerCase()) || note.getContent().toLowerCase().contains(query.toLowerCase())) {
                filteredNoteList.add(note);
            }
        }
        displayNotes(filteredNoteList);
    }

    /**
     *Saves the list of notes to SharedPreferences.
     * Iterates through the noteList, assigns each note's title and content to SharedPreferences,
     * and applies the changes.
     */
    private void saveNotesToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_NOTE_COUNT, noteList.size());
        for (int i = 0; i < noteList.size(); i++) {
            Note note = noteList.get(i);
            editor.putString("note_title_" + i, note.getTitle());
            editor.putString("note_content_" + i, note.getContent());
        }
        editor.apply();
    }
}
