

var encounterTimerStartTime = new Date();
var encounterTimerIntervalId = null;
var encounterCurrentTime = {seconds: 0};
var encounterTimerPaused = {paused: false};

jQuery(document).ready( function () {
    encounterTimerStartTime = new Date();
    encounterTimerIntervalId = window.setInterval(updateTimer, 1000, "#encounter_timer", "#encounter_timer_background", encounterCurrentTime, encounterTimerPaused);
});

function updateTimer(timerId, timerBgId, currTime, timerPaused)
{
    let timer = jQuery(timerId);
    if (timer.length && currTime)
    {
        if (!timerPaused.paused)
        {
            currTime.seconds++;
        }

        let timeSeconds = currTime.seconds % 60;
        let timeMinutes = parseInt(currTime.seconds / 60) % 60;
        let timeHours = parseInt(parseInt(currTime.seconds / 60) / 60);

        if (timeHours > 0)
        {
            timer.html((timeHours < 10 ? ("0" + timeHours).slice(-2) : timeHours) + ":" +
                (timeMinutes < 10 ? ("0" + timeMinutes).slice(-2) : timeMinutes) + ":" + ("0" + timeSeconds).slice(-2));
        }
        else
        {
            timer.html((timeMinutes < 10 ? ("0" + timeMinutes).slice(-2) : timeMinutes) + ":" + ("0" + timeSeconds).slice(-2));
        }

        //modify color if over certain time
        if (currTime.seconds > 3600)
        {
            jQuery(timerBgId).css("background-color", "#FDFEC7");
        }
        else if (currTime.seconds > 1200)
        {
            jQuery(timerBgId).css("background-color", "#DFF0D8");
        }

        //ensure correct button is being displayed
        showEncounterTimerControl(timerPaused.paused, '#encounter_timer_pause', '#encounter_timer_play');
    }
}

// toggle the timer state depending on current state (start / stop)
window.toggleEncounterTimer = function toggleEncounterTimer(pauseId, playId)
{
    if (encounterTimerPaused.paused)
    {// play
        showEncounterTimerControl(false, pauseId, playId);

        encounterTimerPaused.paused = false;
        //encounterTimerIntervalId = window.setInterval(updateTimer, 1000, "#encounter_timer", "#encounter_timer_background", encounterCurrentTime);
    }
    else
    {// stop
        showEncounterTimerControl(true, pauseId, playId);

        encounterTimerPaused.paused = true;
        //window.clearInterval(encounterTimerIntervalId);
    }
}


function showEncounterTimerControl(bPaused, pauseId, playId)
{
    let pause = jQuery(pauseId);
    let play = jQuery(playId);

    if (play.length && pause.length)
    {
        if (bPaused)
        {
            play.css("display", "inline-block");
            pause.css("display", "none");
        }
        else
        {
            play.css("display", "none");
            pause.css("display", "inline-block");
        }
    }
}

window.putEncounterTimeInNote = function putEncounterTimeInNote()
{
    let timer = jQuery("#encounter_timer");
    let activeNote = jQuery($(caseNote));
    if (activeNote.length && timer.length)
    {
        let endTime = new Date();
        let timeStr = timer.html();

        if (timeStr.split(":").length < 3)
        {
            timeStr = "00:" + timeStr;
        }

        noteTxt = activeNote.val();
        noteTxt = noteTxt +
            "Start time: " + encounterTimerStartTime.getHours() + ":" + encounterTimerStartTime.getMinutes() + "\n" +
            "End time: " + endTime.getHours() + ":" + endTime.getMinutes() + "\n" +
            "Duration: " + timeStr + "\n";
        activeNote.val(noteTxt);
        adjustCaseNote();
    }

}