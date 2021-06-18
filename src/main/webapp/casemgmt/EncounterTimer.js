
window.encounterTimer =
{
    startTime: new Date(),
    intervalId: null,
    currentTime: {seconds: 0},
    isPaused: {paused: false},
    updateTimer: function updateTimer(timerId, timerBgId, currTime, timerPaused)
    {
        let timer = jQuery(timerId);
        if (timer.length && currTime) {
            if (!timerPaused.paused) {
                currTime.seconds++;
            }

            let timeSeconds = currTime.seconds % 60;
            let timeMinutes = parseInt(currTime.seconds / 60) % 60;
            let timeHours = parseInt(parseInt(currTime.seconds / 60) / 60);

            if (timeHours > 0) {
                timer.val((timeHours < 10 ? ("0" + timeHours).slice(-2) : timeHours) + ":" +
                    (timeMinutes < 10 ? ("0" + timeMinutes).slice(-2) : timeMinutes) + ":" + ("0" + timeSeconds).slice(-2));
            } else {
                timer.val((timeMinutes < 10 ? ("0" + timeMinutes).slice(-2) : timeMinutes) + ":" + ("0" + timeSeconds).slice(-2));
            }

            //modify color if over certain time
            if (currTime.seconds > 3600) {
                jQuery(timerBgId).css("background-color", "#FDFEC7");
            } else if (currTime.seconds > 1200) {
                jQuery(timerBgId).css("background-color", "#DFF0D8");
            }

            //ensure correct button is being displayed
            encounterTimer.showEncounterTimerControl(timerPaused.paused, '#encounter_timer_pause', '#encounter_timer_play');
        }
    },

    // toggle the timer state depending on current state (start / stop)
    toggleEncounterTimer: function toggleEncounterTimer(pauseId, playId) {
        if (encounterTimer.isPaused.paused) {// play
            this.showEncounterTimerControl(false, pauseId, playId);

            encounterTimer.isPaused.paused = false;
            //encounterTimerIntervalId = window.setInterval(updateTimer, 1000, "#encounter_timer", "#encounter_timer_background", encounterCurrentTime);
        } else {// stop
            this.showEncounterTimerControl(true, pauseId, playId);

            encounterTimer.isPaused.paused = true;
            //window.clearInterval(encounterTimerIntervalId);
        }
    },

    showEncounterTimerControl: function showEncounterTimerControl(bPaused, pauseId, playId) {
        let pause = jQuery(pauseId);
        let play = jQuery(playId);

        if (play.length && pause.length) {
            if (bPaused) {
                play.css("display", "inline-block");
                pause.css("display", "none");
            } else {
                play.css("display", "none");
                pause.css("display", "inline-block");
            }
        }
    }
};

jQuery(document).ready( function () {
    encounterTimer.startTime = new Date();
    encounterTimer.intervalId = window.setInterval(encounterTimer.updateTimer, 1000, "#encounter_timer", "#encounter_timer", encounterTimer.currentTime, encounterTimer.isPaused);
});