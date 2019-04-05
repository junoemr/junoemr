

var encounterTimerStartTime = new Date();
var encounterTimerIntervalId = null;
var encounterCurrentTime = {seconds: 4850};

jQuery(document).ready( function () {
    encounterTimerStartTime = new Date();
    encounterTimerIntervalId = window.setInterval(updateTimer, 1000, "#encounter_timer", encounterCurrentTime);
});

function updateTimer(timerId, currTime)
{
    let timer = jQuery(timerId);
    if (timer.length && currTime)
    {
        currTime.seconds ++;

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
    }
}

// toggle the timer state depending on current state (start / stop)
window.toggleEncounterTimer = function toggleEncounterTimer(pauseId, playId)
{
    let pause = jQuery(pauseId);
    let play = jQuery(playId);

    if (play.length && pause.length)
    {
        if (pause.css("display") == "none")
        {// play
            play.css("display", "none");
            pause.css("display", "inline-block");

            encounterTimerIntervalId = window.setInterval(updateTimer, 1000, "#encounter_timer", encounterCurrentTime);
        }
        else
        {// stop
            play.css("display", "inline-block");
            pause.css("display", "none");

            window.clearInterval(encounterTimerIntervalId);
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