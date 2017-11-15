/*
 (c) 2017 Pear Deck, Inc.
 License: Apache 2
*/

(function () {
	function post(url, data) {
		var req = new XMLHttpRequest();
		req.open("POST", url, true);
		req.setRequestHeader('Content-type', 'application/json');

		req.onreadystatechange = function () {
			if (req.readyState === 4 && req.status == 200) {
				// :tada:
			} else {
				// if a send doesn't succeed, we lose the metrics, but we probably don't want to bother anyone w/ the details.
			}
		};

		req.send(data);
	}

	function setUpAndStartInterval({ aggregatorReportingUrl }) {
		let counterIncrements = {};
		let histogramObservations = {};

		setTimeout(sendUpdates, 100);

		function sendUpdates() {
			var body = JSON.stringify(packageUpdates());

			post(aggregatorReportingUrl, body);

			// clear all metrics. If the most recent send does not succeed, then
			// we will lose that batch of metrics. :shrug:
			counterIncrements = {};
			histogramObservations = {};
		}

		function packageUpdates() {
			//const counterUpdates = Object.values(counterIncrements);
			const histogramUpdates = Object.values(histogramObservations);

			var updates = {...histogramUpdates };
			if(updates && updates[0] && updates[0].observations && updates[0].observations[0] &&
				updates[0].observations[0] > 0)
			{
				return {page_load_time: updates[0].observations[0]};
			}
			return {page_load_time: 0};
		}

		function increment(name, labels, inc) {
			const key = `${name}{${flattenLabels(labels)}}`;
			if (counterIncrements[key]) {
				counterIncrements[key].inc += inc;
			} else {
				counterIncrements[key] = {
					metricName: name,
					metricType: 'counter',
					labels,
					inc: inc,
				};
			}
		}

		function observe(name, labels, observation) {
			const key = name;
			if (histogramObservations[key]) {
				histogramObservations[key].observations.push(observation);
			} else if(key == 'performance_timing_loadEventEnd') {
				histogramObservations[key] = {
					metricName: name,
					metricType: 'histogram',
					labels,
					observations: [observation]
				};
			}
		}

		function flattenLabels(labelsObject) {
			const keys = Object.keys(labelsObject).sort();
			const printed = keys.map((key) => `${key}="${labelsObject[key]}"`);
			return printed.join(',');
		}

		return function (fnName, metricName, labels, value) {
			if (fnName === 'increment') {
				increment(metricName, labels, value);
			} else if (fnName === 'observe') {
				observe(metricName, labels, value);
			} else {
				console.warn("unknown fn name ", fnName);
			}
		};
	}

	function reportNavigationTiming() {
		if (!performance || !performance.timing) {
			console.log("performance.timing not supported");
			return;
		}

		var navigationStart = performance.timing.navigationStart;
		const prometheusAggregator = window[window['PrometheusAggregatorObjectName']];
		var key;
		for (key in performance.timing) {
			if (typeof performance.timing[key] === 'number' && performance.timing[key] > 0) {
				prometheusAggregator('observe', 'performance_timing_' + key, {}, (performance.timing[key] - navigationStart) / 1000.0);
			}
		}
	}

	function start()
	{
		const {q, aggregatorServerRoot} = window[window['PrometheusAggregatorObjectName']];

		const actualFunction = window[window['PrometheusAggregatorObjectName']] = setUpAndStartInterval({aggregatorReportingUrl: aggregatorServerRoot});

		(q || []).forEach((args) => actualFunction(...args ));

		reportNavigationTiming();
	}

	window.addEventListener('load', function()
	{
		setTimeout( start, 500);
	});
}());