package firman.music.app.core.state

import firman.music.app.model.TrackItem

/**
 * Sealed interface representing the different states of the player UI.
 */
sealed interface PlayerUIState {
    /**
     * Represents the state when the player UI displays a list of tracks.
     *
     * @property items The list of track items to be displayed.
     */
    data class Tracks(val items: List<TrackItem> ?= emptyList()) : PlayerUIState

    /**
     * Represents the state when the player UI is in a loading state.
     */
    data object Loading : PlayerUIState
}
