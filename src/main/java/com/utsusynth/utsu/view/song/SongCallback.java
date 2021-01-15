package com.utsusynth.utsu.view.song;

import com.utsusynth.utsu.common.RegionBounds;
import com.utsusynth.utsu.common.data.MutateResponse;
import com.utsusynth.utsu.common.data.NoteData;
import com.utsusynth.utsu.common.data.NoteUpdateData;
import com.utsusynth.utsu.controller.UtsuController.CheckboxType;
import com.utsusynth.utsu.view.song.note.Note;
import javafx.beans.property.BooleanProperty;

import java.util.List;
import java.util.Set;

/**
 * The view can use this interface to communicate with the model by way of the controller.
 */
public interface SongCallback {
    /**
     * Add a one or more notes to the song. Input list must be in order.
     */
    void addNotes(List<NoteData> toAdd);

    /**
     * Remove one or more notes from the song.
     */
    MutateResponse removeNotes(Set<Integer> positions);

    /**
     * Modify a note without changing its position or duration.
     */
    NoteUpdateData modifyNote(NoteData toModify);

    /**
     * Standardizes a section of notes and returns any frontend updates.
     */
    MutateResponse standardizeNotes(int firstPosition, int lastPosition);

    /**
     * Records an action so it can be undone or redone later.
     */
    void recordAction(Runnable redoAction, Runnable undoAction);

    /**
     * Open the note properties editor on the given RegionBounds.
     */
    void openNoteProperties(RegionBounds regionBounds);

    /**
     * Opens the lyric config for a particular note, if present.
     */
    void openLyricConfig(int position);

    /**
     * Clears cache for a section of notes.
     */
    void clearCache(int firstPosition, int lastPosition);

    /**
     * Fetches the value of any global checkbox menu item.
     */
    BooleanProperty getCheckboxValue(CheckboxType checkboxType);

    /**
     * Update the lyrics textbox and real lyrics on main window
     */
    void updateLyricTextBoxes(Note note);
}
