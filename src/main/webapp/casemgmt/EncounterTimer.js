

var encounterTimerStartTime = new Date();
var encounterTimerIntervalId = null;
var encounterCurrentTime = {str: "00:00"};

jQuery(document).ready( function () {
    encounterTimerStartTime = new Date();
    encounterTimerIntervalId = window.setInterval(updateTimer, 1000, "#encounter_timer", encounterCurrentTime);
});

function updateTimer(timerId, currTime)
{
    let timer = jQuery(timerId);
    if (timer.length)
    {
        let timeArray = "";
        if (currTime)
        {
            timeArray = currTime.str.split(":");
        }
        else
        {
            timeArray = timer.html().split(":");
        }

        let timeMintuesString = timeArray[timeArray.length -2];
        let timeSecondsString = timeArray[timeArray.length -1];
        let timeSeconds = parseInt(timeSecondsString) + 1;
        let timeMintues = parseInt(timeMintuesString);
        let timeHours = 0;

        if (timeArray.length > 2)
        {
            timeHours = parseInt(timeArray[0]);
        }

        if (timeSeconds > 59)
        {
            timeSeconds = 0;
            timeMintues += 1;
        }

        if (timeMintues > 59)
        {
            timeMintues = 0;
            timeHours += 1;
        }

        if (timeHours > 0)
        {
            timer.html((timeHours < 10 ? ("0" + timeHours).slice(-2) : timeHours) + ":" +
                (timeMintues < 10 ? ("0" + timeMintues).slice(-2) : timeMintues) + ":" + ("0" + timeSeconds).slice(-2));
        }
        else
        {
            timer.html((timeMintues < 10 ? ("0" + timeMintues).slice(-2) : timeMintues) + ":" + ("0" + timeSeconds).slice(-2));
        }

        //save current time
        currTime.str = timer.html();
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