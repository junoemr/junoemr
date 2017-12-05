/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

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
			const body = JSON.parse(JSON.stringify(packageUpdates()));

			post(aggregatorReportingUrl, body);

			// clear all metrics. If the most recent send does not succeed, then
			// we will lose that batch of metrics. :shrug:
			counterIncrements = {};
			histogramObservations = {};
		}

		function packageUpdates() {
			const counterUpdates = Object.values(counterIncrements);
			const histogramUpdates = Object.values(histogramObservations);

			return [ ...counterUpdates, ...histogramUpdates ];
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
			const key = `${name}{${flattenLabels(labels)}}`;
			if (histogramObservations[key]) {
				histogramObservations[key].observations.push(observation);
			} else {
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
