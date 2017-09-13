
<%--


    Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for
    Centre for Research on Inner City Health, St. Michael's Hospital,
    Toronto, Ontario, Canada

--%>

<%@ taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi"%>

<%@ include file="/casemgmt/taglibs.jsp" %>
<%@page errorPage="/casemgmt/error.jsp" %>
<%@page import="java.util.Enumeration, org.apache.commons.lang.StringEscapeUtils" %>
<%@page import="org.oscarehr.casemgmt.web.formbeans.*, org.oscarehr.casemgmt.model.CaseManagementNote"%>
<%@page import="org.oscarehr.common.dao.UserPropertyDAO, oscar.OscarProperties" %>
<%@page import="org.oscarehr.common.model.UserProperty" %>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="org.oscarehr.util.LoggedInInfo" %>
<%@page import="org.oscarehr.casemgmt.common.Colour" %>
<%@page import="org.oscarehr.common.dao.ProviderDataDao" %>
<%@page import="org.oscarehr.common.model.ProviderData"%>
<%@page import="java.util.List"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<%
	oscar.oscarEncounter.pageUtil.EctSessionBean bean = null;
	String beanName = "casemgmt_oscar_bean" + (String) request.getAttribute("demographicNo");

	pageContext.setAttribute("providerNo",request.getParameter("providerNo"), PageContext.PAGE_SCOPE);
	pageContext.setAttribute("demographicNo",request.getParameter("demographicNo"), PageContext.PAGE_SCOPE);

	org.oscarehr.casemgmt.model.CaseManagementNoteExt cme = new org.oscarehr.casemgmt.model.CaseManagementNoteExt();

	String frmName = "caseManagementEntryForm" + request.getParameter("demographicNo");
	CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean)session.getAttribute(frmName);

	String encTimeMandatoryValue = OscarProperties.getInstance().getProperty("ENCOUNTER_TIME_MANDATORY","false");

%>

<html:html locale="true">
	<head>
		<c:set var="ctx" value="${pageContext.request.contextPath}"	scope="request" />

        <!-- NewRelic page load monitoring script -->
		<script type="text/javascript">
            window.NREUM||(NREUM={}),__nr_require=function(t,n,e){function r(e){if(!n[e]){var o=n[e]={exports:{}};t[e][0].call(o.exports,function(n){var o=t[e][1][n];return r(o||n)},o,o.exports)}return n[e].exports}if("function"==typeof __nr_require)return __nr_require;for(var o=0;o<e.length;o++)r(e[o]);return r}({1:[function(t,n,e){function r(t){try{s.console&&console.log(t)}catch(n){}}var o,i=t("ee"),a=t(15),s={};try{o=localStorage.getItem("__nr_flags").split(","),console&&"function"==typeof console.log&&(s.console=!0,o.indexOf("dev")!==-1&&(s.dev=!0),o.indexOf("nr_dev")!==-1&&(s.nrDev=!0))}catch(c){}s.nrDev&&i.on("internal-error",function(t){r(t.stack)}),s.dev&&i.on("fn-err",function(t,n,e){r(e.stack)}),s.dev&&(r("NR AGENT IN DEVELOPMENT MODE"),r("flags: "+a(s,function(t,n){return t}).join(", ")))},{}],2:[function(t,n,e){function r(t,n,e,r,o){try{d?d-=1:i("err",[o||new UncaughtException(t,n,e)])}catch(s){try{i("ierr",[s,c.now(),!0])}catch(u){}}return"function"==typeof f&&f.apply(this,a(arguments))}function UncaughtException(t,n,e){this.message=t||"Uncaught error with no additional information",this.sourceURL=n,this.line=e}function o(t){i("err",[t,c.now()])}var i=t("handle"),a=t(16),s=t("ee"),c=t("loader"),f=window.onerror,u=!1,d=0;c.features.err=!0,t(1),window.onerror=r;try{throw new Error}catch(l){"stack"in l&&(t(8),t(7),"addEventListener"in window&&t(5),c.xhrWrappable&&t(9),u=!0)}s.on("fn-start",function(t,n,e){u&&(d+=1)}),s.on("fn-err",function(t,n,e){u&&(this.thrown=!0,o(e))}),s.on("fn-end",function(){u&&!this.thrown&&d>0&&(d-=1)}),s.on("internal-error",function(t){i("ierr",[t,c.now(),!0])})},{}],3:[function(t,n,e){t("loader").features.ins=!0},{}],4:[function(t,n,e){function r(t){}if(window.performance&&window.performance.timing&&window.performance.getEntriesByType){var o=t("ee"),i=t("handle"),a=t(8),s=t(7),c="learResourceTimings",f="addEventListener",u="resourcetimingbufferfull",d="bstResource",l="resource",p="-start",h="-end",m="fn"+p,w="fn"+h,v="bstTimer",y="pushState",g=t("loader");g.features.stn=!0,t(6);var b=NREUM.o.EV;o.on(m,function(t,n){var e=t[0];e instanceof b&&(this.bstStart=g.now())}),o.on(w,function(t,n){var e=t[0];e instanceof b&&i("bst",[e,n,this.bstStart,g.now()])}),a.on(m,function(t,n,e){this.bstStart=g.now(),this.bstType=e}),a.on(w,function(t,n){i(v,[n,this.bstStart,g.now(),this.bstType])}),s.on(m,function(){this.bstStart=g.now()}),s.on(w,function(t,n){i(v,[n,this.bstStart,g.now(),"requestAnimationFrame"])}),o.on(y+p,function(t){this.time=g.now(),this.startPath=location.pathname+location.hash}),o.on(y+h,function(t){i("bstHist",[location.pathname+location.hash,this.startPath,this.time])}),f in window.performance&&(window.performance["c"+c]?window.performance[f](u,function(t){i(d,[window.performance.getEntriesByType(l)]),window.performance["c"+c]()},!1):window.performance[f]("webkit"+u,function(t){i(d,[window.performance.getEntriesByType(l)]),window.performance["webkitC"+c]()},!1)),document[f]("scroll",r,{passive:!0}),document[f]("keypress",r,!1),document[f]("click",r,!1)}},{}],5:[function(t,n,e){function r(t){for(var n=t;n&&!n.hasOwnProperty(u);)n=Object.getPrototypeOf(n);n&&o(n)}function o(t){s.inPlace(t,[u,d],"-",i)}function i(t,n){return t[1]}var a=t("ee").get("events"),s=t(18)(a,!0),c=t("gos"),f=XMLHttpRequest,u="addEventListener",d="removeEventListener";n.exports=a,"getPrototypeOf"in Object?(r(document),r(window),r(f.prototype)):f.prototype.hasOwnProperty(u)&&(o(window),o(f.prototype)),a.on(u+"-start",function(t,n){var e=t[1],r=c(e,"nr@wrapped",function(){function t(){if("function"==typeof e.handleEvent)return e.handleEvent.apply(e,arguments)}var n={object:t,"function":e}[typeof e];return n?s(n,"fn-",null,n.name||"anonymous"):e});this.wrapped=t[1]=r}),a.on(d+"-start",function(t){t[1]=this.wrapped||t[1]})},{}],6:[function(t,n,e){var r=t("ee").get("history"),o=t(18)(r);n.exports=r,o.inPlace(window.history,["pushState","replaceState"],"-")},{}],7:[function(t,n,e){var r=t("ee").get("raf"),o=t(18)(r),i="equestAnimationFrame";n.exports=r,o.inPlace(window,["r"+i,"mozR"+i,"webkitR"+i,"msR"+i],"raf-"),r.on("raf-start",function(t){t[0]=o(t[0],"fn-")})},{}],8:[function(t,n,e){function r(t,n,e){t[0]=a(t[0],"fn-",null,e)}function o(t,n,e){this.method=e,this.timerDuration=isNaN(t[1])?0:+t[1],t[0]=a(t[0],"fn-",this,e)}var i=t("ee").get("timer"),a=t(18)(i),s="setTimeout",c="setInterval",f="clearTimeout",u="-start",d="-";n.exports=i,a.inPlace(window,[s,"setImmediate"],s+d),a.inPlace(window,[c],c+d),a.inPlace(window,[f,"clearImmediate"],f+d),i.on(c+u,r),i.on(s+u,o)},{}],9:[function(t,n,e){function r(t,n){d.inPlace(n,["onreadystatechange"],"fn-",s)}function o(){var t=this,n=u.context(t);t.readyState>3&&!n.resolved&&(n.resolved=!0,u.emit("xhr-resolved",[],t)),d.inPlace(t,y,"fn-",s)}function i(t){g.push(t),h&&(x?x.then(a):w?w(a):(E=-E,O.data=E))}function a(){for(var t=0;t<g.length;t++)r([],g[t]);g.length&&(g=[])}function s(t,n){return n}function c(t,n){for(var e in t)n[e]=t[e];return n}t(5);var f=t("ee"),u=f.get("xhr"),d=t(18)(u),l=NREUM.o,p=l.XHR,h=l.MO,m=l.PR,w=l.SI,v="readystatechange",y=["onload","onerror","onabort","onloadstart","onloadend","onprogress","ontimeout"],g=[];n.exports=u;var b=window.XMLHttpRequest=function(t){var n=new p(t);try{u.emit("new-xhr",[n],n),n.addEventListener(v,o,!1)}catch(e){try{u.emit("internal-error",[e])}catch(r){}}return n};if(c(p,b),b.prototype=p.prototype,d.inPlace(b.prototype,["open","send"],"-xhr-",s),u.on("send-xhr-start",function(t,n){r(t,n),i(n)}),u.on("open-xhr-start",r),h){var x=m&&m.resolve();if(!w&&!m){var E=1,O=document.createTextNode(E);new h(a).observe(O,{characterData:!0})}}else f.on("fn-end",function(t){t[0]&&t[0].type===v||a()})},{}],10:[function(t,n,e){function r(t){var n=this.params,e=this.metrics;if(!this.ended){this.ended=!0;for(var r=0;r<d;r++)t.removeEventListener(u[r],this.listener,!1);if(!n.aborted){if(e.duration=a.now()-this.startTime,4===t.readyState){n.status=t.status;var i=o(t,this.lastSize);if(i&&(e.rxSize=i),this.sameOrigin){var c=t.getResponseHeader("X-NewRelic-App-Data");c&&(n.cat=c.split(", ").pop())}}else n.status=0;e.cbTime=this.cbTime,f.emit("xhr-done",[t],t),s("xhr",[n,e,this.startTime])}}}function o(t,n){var e=t.responseType;if("json"===e&&null!==n)return n;var r="arraybuffer"===e||"blob"===e||"json"===e?t.response:t.responseText;return h(r)}function i(t,n){var e=c(n),r=t.params;r.host=e.hostname+":"+e.port,r.pathname=e.pathname,t.sameOrigin=e.sameOrigin}var a=t("loader");if(a.xhrWrappable){var s=t("handle"),c=t(11),f=t("ee"),u=["load","error","abort","timeout"],d=u.length,l=t("id"),p=t(14),h=t(13),m=window.XMLHttpRequest;a.features.xhr=!0,t(9),f.on("new-xhr",function(t){var n=this;n.totalCbs=0,n.called=0,n.cbTime=0,n.end=r,n.ended=!1,n.xhrGuids={},n.lastSize=null,p&&(p>34||p<10)||window.opera||t.addEventListener("progress",function(t){n.lastSize=t.loaded},!1)}),f.on("open-xhr-start",function(t){this.params={method:t[0]},i(this,t[1]),this.metrics={}}),f.on("open-xhr-end",function(t,n){"loader_config"in NREUM&&"xpid"in NREUM.loader_config&&this.sameOrigin&&n.setRequestHeader("X-NewRelic-ID",NREUM.loader_config.xpid)}),f.on("send-xhr-start",function(t,n){var e=this.metrics,r=t[0],o=this;if(e&&r){var i=h(r);i&&(e.txSize=i)}this.startTime=a.now(),this.listener=function(t){try{"abort"===t.type&&(o.params.aborted=!0),("load"!==t.type||o.called===o.totalCbs&&(o.onloadCalled||"function"!=typeof n.onload))&&o.end(n)}catch(e){try{f.emit("internal-error",[e])}catch(r){}}};for(var s=0;s<d;s++)n.addEventListener(u[s],this.listener,!1)}),f.on("xhr-cb-time",function(t,n,e){this.cbTime+=t,n?this.onloadCalled=!0:this.called+=1,this.called!==this.totalCbs||!this.onloadCalled&&"function"==typeof e.onload||this.end(e)}),f.on("xhr-load-added",function(t,n){var e=""+l(t)+!!n;this.xhrGuids&&!this.xhrGuids[e]&&(this.xhrGuids[e]=!0,this.totalCbs+=1)}),f.on("xhr-load-removed",function(t,n){var e=""+l(t)+!!n;this.xhrGuids&&this.xhrGuids[e]&&(delete this.xhrGuids[e],this.totalCbs-=1)}),f.on("addEventListener-end",function(t,n){n instanceof m&&"load"===t[0]&&f.emit("xhr-load-added",[t[1],t[2]],n)}),f.on("removeEventListener-end",function(t,n){n instanceof m&&"load"===t[0]&&f.emit("xhr-load-removed",[t[1],t[2]],n)}),f.on("fn-start",function(t,n,e){n instanceof m&&("onload"===e&&(this.onload=!0),("load"===(t[0]&&t[0].type)||this.onload)&&(this.xhrCbStart=a.now()))}),f.on("fn-end",function(t,n){this.xhrCbStart&&f.emit("xhr-cb-time",[a.now()-this.xhrCbStart,this.onload,n],n)})}},{}],11:[function(t,n,e){n.exports=function(t){var n=document.createElement("a"),e=window.location,r={};n.href=t,r.port=n.port;var o=n.href.split("://");!r.port&&o[1]&&(r.port=o[1].split("/")[0].split("@").pop().split(":")[1]),r.port&&"0"!==r.port||(r.port="https"===o[0]?"443":"80"),r.hostname=n.hostname||e.hostname,r.pathname=n.pathname,r.protocol=o[0],"/"!==r.pathname.charAt(0)&&(r.pathname="/"+r.pathname);var i=!n.protocol||":"===n.protocol||n.protocol===e.protocol,a=n.hostname===document.domain&&n.port===e.port;return r.sameOrigin=i&&(!n.hostname||a),r}},{}],12:[function(t,n,e){function r(){}function o(t,n,e){return function(){return i(t,[f.now()].concat(s(arguments)),n?null:this,e),n?void 0:this}}var i=t("handle"),a=t(15),s=t(16),c=t("ee").get("tracer"),f=t("loader"),u=NREUM;"undefined"==typeof window.newrelic&&(newrelic=u);var d=["setPageViewName","setCustomAttribute","setErrorHandler","finished","addToTrace","inlineHit","addRelease"],l="api-",p=l+"ixn-";a(d,function(t,n){u[n]=o(l+n,!0,"api")}),u.addPageAction=o(l+"addPageAction",!0),u.setCurrentRouteName=o(l+"routeName",!0),n.exports=newrelic,u.interaction=function(){return(new r).get()};var h=r.prototype={createTracer:function(t,n){var e={},r=this,o="function"==typeof n;return i(p+"tracer",[f.now(),t,e],r),function(){if(c.emit((o?"":"no-")+"fn-start",[f.now(),r,o],e),o)try{return n.apply(this,arguments)}finally{c.emit("fn-end",[f.now()],e)}}}};a("setName,setAttribute,save,ignore,onEnd,getContext,end,get".split(","),function(t,n){h[n]=o(p+n)}),newrelic.noticeError=function(t){"string"==typeof t&&(t=new Error(t)),i("err",[t,f.now()])}},{}],13:[function(t,n,e){n.exports=function(t){if("string"==typeof t&&t.length)return t.length;if("object"==typeof t){if("undefined"!=typeof ArrayBuffer&&t instanceof ArrayBuffer&&t.byteLength)return t.byteLength;if("undefined"!=typeof Blob&&t instanceof Blob&&t.size)return t.size;if(!("undefined"!=typeof FormData&&t instanceof FormData))try{return JSON.stringify(t).length}catch(n){return}}}},{}],14:[function(t,n,e){var r=0,o=navigator.userAgent.match(/Firefox[\/\s](\d+\.\d+)/);o&&(r=+o[1]),n.exports=r},{}],15:[function(t,n,e){function r(t,n){var e=[],r="",i=0;for(r in t)o.call(t,r)&&(e[i]=n(r,t[r]),i+=1);return e}var o=Object.prototype.hasOwnProperty;n.exports=r},{}],16:[function(t,n,e){function r(t,n,e){n||(n=0),"undefined"==typeof e&&(e=t?t.length:0);for(var r=-1,o=e-n||0,i=Array(o<0?0:o);++r<o;)i[r]=t[n+r];return i}n.exports=r},{}],17:[function(t,n,e){n.exports={exists:"undefined"!=typeof window.performance&&window.performance.timing&&"undefined"!=typeof window.performance.timing.navigationStart}},{}],18:[function(t,n,e){function r(t){return!(t&&t instanceof Function&&t.apply&&!t[a])}var o=t("ee"),i=t(16),a="nr@original",s=Object.prototype.hasOwnProperty,c=!1;n.exports=function(t,n){function e(t,n,e,o){function nrWrapper(){var r,a,s,c;try{a=this,r=i(arguments),s="function"==typeof e?e(r,a):e||{}}catch(f){l([f,"",[r,a,o],s])}u(n+"start",[r,a,o],s);try{return c=t.apply(a,r)}catch(d){throw u(n+"err",[r,a,d],s),d}finally{u(n+"end",[r,a,c],s)}}return r(t)?t:(n||(n=""),nrWrapper[a]=t,d(t,nrWrapper),nrWrapper)}function f(t,n,o,i){o||(o="");var a,s,c,f="-"===o.charAt(0);for(c=0;c<n.length;c++)s=n[c],a=t[s],r(a)||(t[s]=e(a,f?s+o:o,i,s))}function u(e,r,o){if(!c||n){var i=c;c=!0;try{t.emit(e,r,o,n)}catch(a){l([a,e,r,o])}c=i}}function d(t,n){if(Object.defineProperty&&Object.keys)try{var e=Object.keys(t);return e.forEach(function(e){Object.defineProperty(n,e,{get:function(){return t[e]},set:function(n){return t[e]=n,n}})}),n}catch(r){l([r])}for(var o in t)s.call(t,o)&&(n[o]=t[o]);return n}function l(n){try{t.emit("internal-error",n)}catch(e){}}return t||(t=o),e.inPlace=f,e.flag=a,e}},{}],ee:[function(t,n,e){function r(){}function o(t){function n(t){return t&&t instanceof r?t:t?c(t,s,i):i()}function e(e,r,o,i){if(!l.aborted||i){t&&t(e,r,o);for(var a=n(o),s=h(e),c=s.length,f=0;f<c;f++)s[f].apply(a,r);var d=u[y[e]];return d&&d.push([g,e,r,a]),a}}function p(t,n){v[t]=h(t).concat(n)}function h(t){return v[t]||[]}function m(t){return d[t]=d[t]||o(e)}function w(t,n){f(t,function(t,e){n=n||"feature",y[e]=n,n in u||(u[n]=[])})}var v={},y={},g={on:p,emit:e,get:m,listeners:h,context:n,buffer:w,abort:a,aborted:!1};return g}function i(){return new r}function a(){(u.api||u.feature)&&(l.aborted=!0,u=l.backlog={})}var s="nr@context",c=t("gos"),f=t(15),u={},d={},l=n.exports=o();l.backlog=u},{}],gos:[function(t,n,e){function r(t,n,e){if(o.call(t,n))return t[n];var r=e();if(Object.defineProperty&&Object.keys)try{return Object.defineProperty(t,n,{value:r,writable:!0,enumerable:!1}),r}catch(i){}return t[n]=r,r}var o=Object.prototype.hasOwnProperty;n.exports=r},{}],handle:[function(t,n,e){function r(t,n,e,r){o.buffer([t],r),o.emit(t,n,e)}var o=t("ee").get("handle");n.exports=r,r.ee=o},{}],id:[function(t,n,e){function r(t){var n=typeof t;return!t||"object"!==n&&"function"!==n?-1:t===window?0:a(t,i,function(){return o++})}var o=1,i="nr@id",a=t("gos");n.exports=r},{}],loader:[function(t,n,e){function r(){if(!x++){var t=b.info=NREUM.info,n=l.getElementsByTagName("script")[0];if(setTimeout(u.abort,3e4),!(t&&t.licenseKey&&t.applicationID&&n))return u.abort();f(y,function(n,e){t[n]||(t[n]=e)}),c("mark",["onload",a()+b.offset],null,"api");var e=l.createElement("script");e.src="https://"+t.agent,n.parentNode.insertBefore(e,n)}}function o(){"complete"===l.readyState&&i()}function i(){c("mark",["domContent",a()+b.offset],null,"api")}function a(){return E.exists&&performance.now?Math.round(performance.now()):(s=Math.max((new Date).getTime(),s))-b.offset}var s=(new Date).getTime(),c=t("handle"),f=t(15),u=t("ee"),d=window,l=d.document,p="addEventListener",h="attachEvent",m=d.XMLHttpRequest,w=m&&m.prototype;NREUM.o={ST:setTimeout,SI:d.setImmediate,CT:clearTimeout,XHR:m,REQ:d.Request,EV:d.Event,PR:d.Promise,MO:d.MutationObserver};var v=""+location,y={beacon:"bam.nr-data.net",errorBeacon:"bam.nr-data.net",agent:"js-agent.newrelic.com/nr-1044.min.js"},g=m&&w&&w[p]&&!/CriOS/.test(navigator.userAgent),b=n.exports={offset:s,now:a,origin:v,features:{},xhrWrappable:g};t(12),l[p]?(l[p]("DOMContentLoaded",i,!1),d[p]("load",r,!1)):(l[h]("onreadystatechange",o),d[h]("onload",r)),c("mark",["firstbyte",s],null,"api");var x=0,E=t(17)},{}]},{},["loader",2,10,4,3]);
            ;NREUM.info={beacon:"bam.nr-data.net",errorBeacon:"bam.nr-data.net",licenseKey:"19a33561f4",applicationID:"63409248",sa:1}
		</script>

		<link rel="stylesheet" href="<c:out value="${ctx}"/>/css/casemgmt.css" type="text/css">
		<link rel="stylesheet" href="<c:out value="${ctx}"/>/oscarEncounter/encounterStyles.css" type="text/css">
		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}"/>/css/print.css" media="print">

		<!--
<script src="<c:out value="${ctx}/js/jquery.js"/>"></script>
 -->
		<script src="<c:out value="${ctx}/js/jquery-1.7.1.min.js"/>"></script>
		<script language="javascript">
            jQuery.noConflict();
		</script>

		<script src="<c:out value="${ctx}"/>/share/javascript/prototype.js" type="text/javascript"></script>
		<script src="<c:out value="${ctx}"/>/share/javascript/scriptaculous.js" type="text/javascript"></script>

		<script type="text/javascript" src="<c:out value="${ctx}"/>/js/messenger/messenger.js"> </script>
		<script type="text/javascript" src="<c:out value="${ctx}"/>/js/messenger/messenger-theme-future.js"> </script>
		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}"/>/js/messenger/messenger.css"> </link>
		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}"/>/js/messenger/messenger-theme-future.css"> </link>

		<script type="text/javascript" src="newEncounterLayout.js.jsp"> </script>

			<%-- for popup menu of forms --%>
		<script src="<c:out value="${ctx}"/>/share/javascript/popupmenu.js" type="text/javascript"></script>
		<script src="<c:out value="${ctx}"/>/share/javascript/menutility.js" type="text/javascript"></script>

		<!-- library for rounded elements -->
		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}/share/css/niftyCorners.css" />">
		<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/nifty.js"/>"></script>

		<!-- calendar stylesheet -->
		<link rel="stylesheet" type="text/css" media="all" href="<c:out value="${ctx}"/>/share/calendar/calendar.css" title="win2k-cold-1">

		<!-- main calendar program -->
		<script type="text/javascript" src="<c:out value="${ctx}"/>/share/calendar/calendar.js"></script>

		<!-- language for the calendar -->
		<script type="text/javascript" src="<c:out value="${ctx}"/>/share/calendar/lang/<bean:message key="global.javascript.calendar"/>"></script>

		<!-- the following script defines the Calendar.setup helper function, which makes adding a calendar a matter of 1 or 2 lines of code. -->
		<script type="text/javascript" src="<c:out value="${ctx}"/>/share/calendar/calendar-setup.js"></script>

		<!-- js window size utility funcs since prototype's funcs are buggy in ie6 -->
		<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/screen.js"/>"></script>

		<!-- scriptaculous based select box -->
		<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/select.js"/>"></script>

		<!-- phr popups -->
		<script type="text/javascript" src="<c:out value="${ctx}/phr/phr.js"/>"></script>


		<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}/css/oscarRx.css" />">


		<script type="text/javascript">
            var Colour = {
                prevention: '<%=Colour.getInstance().prevention%>',
                tickler: '<%=Colour.getInstance().tickler%>',
                disease: '<%=Colour.getInstance().disease%>',
                forms: '<%=Colour.getInstance().forms%>',
                eForms: '<%=Colour.getInstance().eForms%>',
                documents: '<%=Colour.getInstance().documents%>',
                labs: '<%=Colour.getInstance().labs%>',
                messages: '<%=Colour.getInstance().messages%>',
                measurements: '<%=Colour.getInstance().measurements%>',
                consultation: '<%=Colour.getInstance().consultation%>',
                allergy: '<%=Colour.getInstance().allergy%>',
                rx: '<%=Colour.getInstance().rx%>',
                omed: '<%=Colour.getInstance().omed%>',
                riskFactors: '<%=Colour.getInstance().riskFactors%>',
                familyHistory: '<%=Colour.getInstance().familyHistory%>',
                unresolvedIssues: '<%=Colour.getInstance().unresolvedIssues%>',
                resolvedIssues: '<%=Colour.getInstance().resolvedIssues%>',
                episode: '<%=Colour.getInstance().episode%>',
                pregancies: '<%=Colour.getInstance().episode%>'
            };
		</script>

		<!--js code for newCaseManagementView.jsp -->
		<script type="text/javascript" src="<c:out value="${ctx}/js/newCaseManagementView.js.jsp"/>"></script>

		<% if (OscarProperties.getInstance().getBooleanProperty("note_program_ui_enabled", "true")) { %>
		<link rel="stylesheet" href="<c:out value="${ctx}/casemgmt/noteProgram.css" />" />
		<script type="text/javascript" src="<c:out value="${ctx}/casemgmt/noteProgram.js" />"></script>
		<% }

			LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		%>

		<script type="text/javascript">

            jQuery(document).ready(function() {
                <%
       if( loggedInInfo.getLoggedInProvider().getProviderType().equals("resident"))  {
     %>


                jQuery("input[name='reviewed']").change(function() {

                        if( jQuery("input[name='reviewed']:checked").val() == "true") {
                            if( jQuery(".supervisor").is(":visible") ) {
                                jQuery(".supervisor").slideUp(300);
                            }
                            jQuery(".reviewer").slideDown(600);
                            jQuery("#reviewer").focus();

                        }
                        else {
                            if( jQuery(".reviewer").is(":visible") ) {
                                jQuery(".reviewer").slideUp(300);
                            }
                            jQuery(".supervisor").slideDown(600);
                            jQuery("#supervisor").focus();
                        }
                    }

                );
                <%}%>
            });



            function assembleMainChartParams(displayFullChart) {

                var params = "method=edit&ajaxview=ajaxView&fullChart=" + displayFullChart;
                <%
                  Enumeration<String>enumerator = request.getParameterNames();
                  String paramName, paramValue;
                  while( enumerator.hasMoreElements() ) {
                     paramName = enumerator.nextElement();
                     if( paramName.equals("method") || paramName.equals("fullChart") ) {
                         continue;
                     }

                     paramValue = request.getParameter(paramName);

                 %>
                params += "&<%=paramName%>=<%=StringEscapeUtils.escapeJavaScript(paramValue)%>";
                <%

                 }
               %>

                return params;
            }

            function reorderNavBarElements(idToMove, afterThisId) {
                var clone = jQuery("#"+idToMove).clone();
                jQuery("#"+idToMove).remove();
                clone.insertAfter(jQuery("#"+afterThisId));
            }

            function reorderNavBarElementsBefore(idToMove, beforeThisId) {
                var clone = jQuery("#"+idToMove).clone();
                jQuery("#"+idToMove).remove();
                clone.insertBefore(jQuery("#"+beforeThisId));
            }

            function makeElement(type, attributes) {
                var element = document.createElement(type);
                if (attributes != null) {
                    for (var i in attributes) {
                        element.setAttribute(i, attributes[i]);
                    }
                }
                return element;
            }

            function insertAfter( referenceNode, newNode )
            {
                referenceNode.parentNode.insertBefore( newNode, referenceNode.nextSibling );
            }


            function addCppRow(rowNumber) {
                if(!document.getElementById("divR" + rowNumber)) {
                    var newDiv = makeElement('div',{'style':'width: 100%; height: 75px; margin-top: 0px; background-color: #FFFFFF;','id':'divR'+rowNumber});

                    var i1 = makeElement('div',{'id':'divR' + rowNumber + 'I1','class':'topBox','style':'clear: left; float: left; width: 49%; margin-left: 3px;height: inherit;'});
                    var i2 = makeElement('div',{'id':'divR' + rowNumber + 'I2','class':'topBox','style':'clear: right; float: right; width: 49%; margin-right: 3px;height: inherit;'});
                    newDiv.appendChild(i1);
                    newDiv.appendChild(i2);
                    var prevRow = document.getElementById("divR"+(rowNumber-1));
                    insertAfter(prevRow,newDiv);
                }
            }

            function removeCppRow(rowNumber) {
                jQuery("#divR"+rowNumber).remove();
            }

            function popColumn(url,div,params, navBar, navBarObj) {
                params = "reloadURL=" + url + "&numToDisplay=6&cmd=" + params;

                var objAjax = new Ajax.Request (
                    url,
                    {
                        method: 'post',
                        postBody: params,
                        evalScripts: true,
                        onSuccess: function(request) {
                            $(div).update(request.responseText);

                            if( $("leftColLoader") != null )
                                Element.remove("leftColLoader");

                            if( $("rightColLoader") != null )
                                Element.remove("rightColLoader");
                        },
                        onFailure: function(request) {
                            $(div).innerHTML = "<h3>" + div + "</h3>Error: " + request.status;
                        }
                    }

                );
            };

            function addLeftNavDiv(name) {
                var div = document.createElement("div");
                div.className = "leftBox";
                div.style.display = "block";
                div.style.visiblity = "hidden";
                div.id = name;
                $("leftNavBar").appendChild(div);

            }

            function addRightNavDiv(name) {
                var div = document.createElement("div");
                div.className = "leftBox";
                div.style.display = "block";
                div.style.visiblity = "hidden";
                div.id = name;
                $("rightNavBar").appendChild(div);

            }

            function removeNavDiv(name) {
                var tmpEl = document.getElementById(name);
                tmpEl.parentNode.removeChild(tmpEl);
            }

            function reloadNav(name) {
                var url = jQuery("#" + name + " input[name='reloadUrl']").val();
                popColumn(url,name,name,null,null);
            }

            function addPrintOption(name,bean) {
                var test1Str = "<img style=\"cursor: pointer;\" title=\"Print "+name+"\" id=\"img"+name+"\" alt=\"Print "+name+"\" onclick=\"return printInfo(this, 'extPrint"+name+"');\" src=\"" + ctx + "/oscarEncounter/graphics/printer.png\">&nbsp;"+name;
                jQuery("#printDateRow").before("<tr><td></td><td>" + test1Str + "</tr></tr>");
                jQuery("form[name='caseManagementEntryForm']").append("<input name=\"extPrint"+name+"\" id=\"extPrint"+name+"\" value=\"false\" type=\"hidden\"/>");
                jQuery.ajax({ url: ctx+"/casemgmt/ExtPrintRegistry.do?method=register&name="+name+"&bean="+bean, async:false, success: function(data){
                }});
            }

            <%if(request.getParameter("appointmentNo") != null && request.getParameter("appointmentNo").length()>0) { %>
            var appointmentNo = <%=request.getParameter("appointmentNo")%>;
            <% } else { %>
            var appointmentNo = 0;
            <%}%>

            var savedNoteId=0;
		</script>

		<script language="JavaScript" src='<c:out value="${ctx}"/>/jspspellcheck/spellcheck-caller.js'></script>
		<script>
            function spellCheck()
            {
                // Build an array of form elements (not there values)
                var elements = new Array(0);

                // Your form elements that you want to have spell checked
                elements[elements.length] = document.getElementById(caseNote);

                // Start the spell checker
                startSpellCheck(ctx+'/jspspellcheck/',elements);

            }
		</script>

		<!-- set size of window if customized in preferences -->
		<%
			UserPropertyDAO uPropDao = (UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");

			String providerNo=loggedInInfo.getLoggedInProviderNo();
			UserProperty widthP = uPropDao.getProp(providerNo, "encounterWindowWidth");
			UserProperty heightP = uPropDao.getProp(providerNo, "encounterWindowHeight");
			UserProperty maximizeP = uPropDao.getProp(providerNo, "encounterWindowMaximize");

			if(maximizeP != null && maximizeP.getValue().equals("yes")) {
		%><script> jQuery(document).ready(function(){window.resizeTo(screen.width,screen.height);});</script>
		<%
		} else if(widthP != null && widthP.getValue().length()>0 && heightP != null && heightP.getValue().length()>0) {
			String width = widthP.getValue();
			String height = heightP.getValue();
		%>
		<script> jQuery(document).ready(function(){

                window.resizeTo(<%=width%>,<%=height%>);


            }
        );



		</script>
		<%
			}
		%>
		<oscar:customInterface section="cme" />

		<style type="text/css">

			/*CPP Format */
			li.cpp {
				color: #000000;
				font-family: arial, sans-serif;
				text-overflow: ellipsis;
				overflow: hidden;
			}

			/*Note format */
			div.newNote {
				color: #000000;
				font-family: arial, sans-serif;
				font-size: 0.8em;
				margin: 5px 0px 5px 5px;
				float: left;
				width: 98%;
			}

			div.newNote pre {
				color: #000000;
				font-family: arial, sans-serif;
				margin: 0px 3px 0px 3px;
				width: 100%;
				clear: left;
			}

			div.note {
				color: #000000;
				font-family: arial, sans-serif;
				margin: 3px 0px 3px 5px;
				float: left;
				width: 98%;
			}

			div.note pre {
				color: #000000;
				font-family: arial, sans-serif;
				margin: 0px 3px 0px 3px;
				width: 100%;
				clear: left;
			}

			.sig {
				background-color: #CCCCFF;
				color: #000000;
				width: 100%;
				font-size: 9px;
			}

			.txtArea {
				font-family: arial, sans-serif;
				font-size: 1.0em;
				width: 99%;
				rows: 10;
				overflow: hidden;
				border: none;
				font-family: arial, sans-serif;
				margin: 0px 3px 0px 3px;
			}

			p.passwd {
				margin: 0px 3px 0px 3px;
			}

			/* span formatting for measurements div found in ajax call */
			span.measureCol1 {
				float: left;
				width: 50px;
			}

			span.measureCol2 {
				float: left;
				width: 55px;
			}

			span.measureCol3 {
				float: left;
			}

			.topLinks {
				color: black;
				text-decoration: none;
				font-size: 9px;
			}

			.topLinkhover {
				color: blue;
				text-decoration: underline;
			}

			/* formatting for navbar */
			.links {
				color: blue;
				text-decoration: none;
				font-size: 9px;
			}

			.linkhover {
				color: black;
				text-decoration: underline;
			}

			/* template styles*/
			.enTemplate_name_auto_complete {
				width: 350px;
				background: #fff;
				font-size: 9px;
				text-align: left;
			}

			.enTemplate_name_auto_complete ul {
				border: 1px solid #888;
				margin: 0;
				padding: 0;
				width: 100%;
				list-style-type: square;
				list-style-position: inside;
			}

			.enTemplate_name_auto_complete ul li {
				margin: 0;
				padding: 3px;
			}

			.enTemplate_name_auto_complete ul li.selected {
				background-color: #ffb;
				text-decoration: underline;
			}

			.enTemplate_name_auto_complete ul strong.highlight {
				color: #800;
				margin: 0;
				padding: 0;
			}

			/* CPP textareas */
			.rowOne {
				height: <%--<nested:write name="rowOneSize"/>--%>10px;
				width: 97%;
				overflow: auto;
			}

			.rowTwo {
				height: <%--<nested:write name="rowTwoSize"/>--%>10px;
				width: 97%;
				margin-left: 4px;
				overflow: auto;
			}

			/* Encounter type select box */
			div.autocomplete {
				position: absolute;
				width: 400px;
				background-color: white;
				border: 1px solid #ccc;
				margin: 0px;
				padding: 0px;
				font-size: 9px;
				text-align: left;
				max-height: 200px;
				overflow: auto;
			}

			div.autocomplete ul {
				list-style-type: none;
				margin: 0px;
				padding: 0px;
			}

			div.autocomplete ul li.selected {
				background-color: #EAF2FB;
			}

			div.autocomplete ul li {
				list-style-type: none;
				display: block;
				margin: 0;
				padding: 2px;
				cursor: pointer;
			}

			.encTypeCombo /* look&feel of scriptaculous select box*/ {
				margin: 0px; /* 5px 10px 0px;*/
				font-family: Verdana, Geneva, Arial, Helvetica, sans-serif;
				font-size: 9px;
				width: 200px;
				text-align: left;
				vertical-align: middle;
				background: #FFFFFF
				url('<c:out value="${ctx}"/>/images/downarrow_inv.gif') no-repeat
				right;
				height: 18px;
				cursor: pointer;
				border: 1px solid #ccc;
				color: #000000;
			}

			.printOps {
				background-color: #CCCCFF;
				font-size: 9px;
				position: absolute;
				display: none;
				z-index: 1;
				width: 200px;
				right: 100px;
				bottom: 200px;
			}

			.showEdContainer {
				position: absolute;
				display: none;
				z-index: 100;
				right: 100px;
				bottom: 200px;
				background-color: transparent;
				font-size: 8px;
				/*border: thin ridge black;*/
				text-align: center;
			}

			.showEdPosition {
				display: table-cell;
				vertical-align: middle;
			}

			.showEdContent { /*border: thin ridge black;*/
				background-color: #CCCCFF;
				font-size: 9px;
				position: absolute;
				display: none;
				z-index: 200;
				right: 100px;
				bottom: 200px;
				text-align: center;
			}

			.showResident {
				left: 0;
				top: 0;
				/*transform: translate(100%, 100%);*/
				min-width: 100%;
				min-height: 100%;
				background: rgba(239,250,250,0.6);

				position: absolute;
				display: none;
				z-index: 300;
				text-align: center;
				border-style: ridge;
			}

			.showResidentBorder {
				background: rgba(239,250,250,1);
				border-style: ridge;
				text-align: center;
				width: 45%;
				height:auto;
				margin: 40% auto;
				position:relative;
			}

			.showResidentContent {
				background: rgba(13,117,173,1);
				text-align: center;
				width:auto;
				height: auto;
				margin: 2% auto;
				border-style: inset;
				position: relative;
			}

			.residentText {
				font-family: "Times New Roman", Times, serif;
				font-style: italic;
			}

			.supervisor {
			}

			.reviewer {
			}
		</style>

		<html:base />
		<title><bean:message key="oscarEncounter.Index.title" /> - <oscar:nameage
				demographicNo="<%=request.getParameter(\"demographicNo\")%>" /></title>
		<script type="text/javascript">
            ctx = "<c:out value="${ctx}"/>";
            demographicNo = "<c:out value="${demographicNo}"/>";
            providerNo = "<c:out value="${providerNo}"/>";

            socHistoryLabel = "oscarEncounter.socHistory.title";
            medHistoryLabel = "oscarEncounter.medHistory.title";
            onGoingLabel = "oscarEncounter.onGoing.title";;
            remindersLabel = "oscarEncounter.reminders.title";
            oMedsLabel = "oscarEncounter.oMeds.title";
            famHistoryLabel = "oscarEncounter.famHistory.title";
            riskFactorsLabel = "oscarEncounter.riskFactors.title";

            diagnosticNotesLabel = "oscarEncounter.eyeform.diagnosticNotes.title";
            pastOcularHistoryLabel = "oscarEncounter.eyeform.pastOcularHistory.title";
            patientLogLabel = "oscarEncounter.eyeform.patientLog.title";
            ocularMedicationsLabel = "oscarEncounter.eyeform.ocularMedications.title";
            currentHistoryLabel = "oscarEncounter.eyeform.currentHistory.title";

            quickChartMsg = "<bean:message key="oscarEncounter.quickChart.msg"/>";
            fullChartMsg = "<bean:message key="oscarEncounter.fullChart.msg"/>";
            insertTemplateError = "<bean:message key="oscarEncounter.templateError.msg"/>";
            unsavedNoteWarning = "<bean:message key="oscarEncounter.unsavedNoteWarning.msg"/>";
            sessionExpiredError = "<bean:message key="oscarEncounter.sessionExpiredError.msg"/>";
            unlockNoteError = "<bean:message key="oscarEncounter.unlockNoteError.msg"/>";
            filterError = "<bean:message key="oscarEncounter.filterError.msg"/>";
            pastObservationDateError = "<bean:message key="oscarEncounter.pastObservationDateError.msg"/>";
            encTimeError = "<bean:message key="oscarEncounter.encounterTimeError.msg"/>";
            encMinError = "<bean:message key="oscarEncounter.encounterMinuteError.msg"/>";
            assignIssueError = "<bean:message key="oscarEncounter.assignIssueError.msg"/>";
            assignObservationDateError = "<bean:message key="oscarEncounter.assignObservationDateError.msg"/>";

            encTimeMandatoryMsg = "<bean:message key="oscarEncounter.encounterTimeMandatory.msg"/>";
            encTimeMandatory = <%=encTimeMandatoryValue%>;

            assignEncTypeError = "<bean:message key="oscarEncounter.assignEncTypeError.msg"/>";
            savingNoteError = "<bean:message key="oscarEncounter.savingNoteError.msg"/>";
            changeIssueMsg = "<bean:message key="oscarEncounter.change.title"/>";
            closeWithoutSaveMsg = "<bean:message key="oscarEncounter.closeWithoutSave.msg"/>";
            pickIssueMsg = "<bean:message key="oscarEncounter.pickIssue.msg"/>";
            assignIssueMsg = "<bean:message key="oscarEncounter.assign.title"/>";
            updateIssueError = "<bean:message key="oscarEncounter.updateIssueError.msg"/>";
            unsavedNoteMsg = "<bean:message key="oscarEncounter.unsavedNote.msg"/>";
            printDateMsg = "<bean:message key="oscarEncounter.printDate.msg"/>";
            printDateOrderMsg = "<bean:message key="oscarEncounter.printDateOrder.msg"/>";
            nothing2PrintMsg = "<bean:message key="oscarEncounter.nothingToPrint.msg"/>";
            editUnsignedMsg = "<bean:message key="oscarEncounter.editUnsignedNote.msg"/>";
            msgDraftSaved = "<bean:message key="oscarEncounter.draftSaved.msg"/>";
            msgPasswd = "<bean:message key="Logon.passWord"/>";
            btnMsgUnlock = "<bean:message key="oscarEncounter.Index.btnUnLock"/>";
            editLabel = "<bean:message key="oscarEncounter.edit.msgEdit"/>";
            annotationLabel = "<bean:message key="oscarEncounter.Index.btnAnnotation"/>";
            month[0] = "<bean:message key="share.CalendarPopUp.msgJan"/>";
            month[1] = "<bean:message key="share.CalendarPopUp.msgFeb"/>";
            month[2] = "<bean:message key="share.CalendarPopUp.msgMar"/>";
            month[3] = "<bean:message key="share.CalendarPopUp.msgApr"/>";
            month[4] = "<bean:message key="share.CalendarPopUp.msgMay"/>";
            month[5] = "<bean:message key="share.CalendarPopUp.msgJun"/>";
            month[6] = "<bean:message key="share.CalendarPopUp.msgJul"/>";
            month[7] = "<bean:message key="share.CalendarPopUp.msgAug"/>";
            month[8] = "<bean:message key="share.CalendarPopUp.msgSep"/>";
            month[9] = "<bean:message key="share.CalendarPopUp.msgOct"/>";
            month[10] = "<bean:message key="share.CalendarPopUp.msgNov"/>";
            month[11] = "<bean:message key="share.CalendarPopUp.msgDec"/>";

            function init() {
                //scrollDownInnerBar();
                viewFullChart(false);
                showIssueNotes();

                var navBars = new navBarLoader();
                navBars.load();

                monitorNavBars(null);

                Element.observe(window, "resize", monitorNavBars);

                if(!NiftyCheck()) {
                    return;
                }

                Rounded("div.showEdContent","all","transparent","#CCCCCC","big border #000000");
                Rounded("div.printOps","all","transparent","#CCCCCC","big border #000000");
                Calendar.setup({ inputField : "printStartDate", ifFormat : "%d-%b-%Y", showsTime :false, button : "printStartDate_cal", singleClick : true, step : 1 });
                Calendar.setup({ inputField : "printEndDate", ifFormat : "%d-%b-%Y", showsTime :false, button : "printEndDate_cal", singleClick : true, step : 1 });

                <c:url value="/CaseManagementEntry.do" var="issueURLCPP">
                <c:param name="method" value="issueList"/>
                <c:param name="demographicNo" value="${demographicNo}" />
                <c:param name="providerNo" value="${providerNo}" />
                <c:param name="all" value="true" />
                </c:url>
                var issueAutoCompleterCPP = new Ajax.Autocompleter("issueAutocompleteCPP", "issueAutocompleteListCPP", "<c:out value="${issueURLCPP}" />", {minChars: 3, indicator: 'busy2', afterUpdateElement: addIssue2CPP, onShow: autoCompleteShowMenuCPP, onHide: autoCompleteHideMenuCPP});

                <nested:notEmpty name="DateError">
                alert("<nested:write name="DateError"/>");
                </nested:notEmpty>
            }

            function doscroll(){
                x=document.body.scrollHeight;
                x=x+99999
                window.scrollTo(0,x);
            }

            window.onbeforeunload = onClosing;


		</script>
	</head>
	<body id="body" style="margin: 0px;">

		<%--
        <caisi:isModuleLoad moduleName="eaaps.enabled">
            <div id="eaaps" style="display: none;">
                  <div id="basic-template">
                  <a class="ui-notify-cross ui-notify-close" href="#">x</a>
                  <h1>TITLE</h1>
                  <p>text</p>
           </div>

                <!-- jsp : include page="/eaaps/status.jsp">< / jsp : include -->
            </div>
        </caisi:isModuleLoad>
        --%>

	<div id="header">
		<tiles:insert attribute="header" />
	</div>

	<div id="rightNavBar"
		 style="display: inline; float: right; width: 20%; margin-left: -3px;">
		<tiles:insert attribute="rightNavigation" />
	</div>

	<div id="leftNavBar" style="display: inline; float: left; width: 20%;">
		<tiles:insert attribute="leftNavigation" />
	</div>

	<div id="content"
		 style="display: inline; float: left; width: 60%; background-color: #CCCCFF;">
		<tiles:insert attribute="body" />
	</div>

	<!-- Display Integrated Data -->
	<div id="showIntegratedNote" class="showEdContent" style="height:325px;">
		<div id="integratedNoteWrapper" style="position:relative;width:99.5%;height:320px">
			<div id="integratedNoteTitle"></div>
			<textarea style="margin: 10px;" cols="50" rows="15" id="integratedNoteTxt" name="integratedNoteTxt" readonly></textarea>
			<div id="integratedNoteDetails" style="text-align:left;padding-left:4px;font-size:10px;"></div>

			<span style="position:absolute;right:10px;">
					<input type="image"
						   src="<c:out value="${ctx}/oscarEncounter/graphics/system-log-out.png"/>"
						   title='<bean:message key="global.btnExit"/>'
						   onclick="this.focus();$('channel').style.visibility ='visible';$('showIntegratedNote').style.display='none';return false;">
		</span>

		</div><!-- integratedNoteWrapper -->
	</div><!-- showIntegratedNote -->

	<!-- hovering divs -->
	<div id="showEditNote" class="showEdContent">
		<form id="frmIssueNotes" action="" method="post"
			  onsubmit="return updateCPPNote();">
			<input type="hidden" id="reloadUrl" name="reloadUrl" value="">
			<input type="hidden" id="containerDiv" name="containerDiv" value="">
			<input type="hidden" id="issueChange" name="issueChange" value="">
			<input type="hidden" id="archived" name="archived" value="false">
			<input type="hidden" id="annotation_attrib" name="annotation_attrib">
			<div id="winTitle"></div>
			<textarea style="margin: 10px;" cols="50" rows="15" id="noteEditTxt"
					  name="value"></textarea>
			<br>

			<table>
				<tr id="Itemproblemdescription">
					<td><bean:message
							key="oscarEncounter.problemdescription.title" />:</td>
					<td><input type="text" id="problemdescription"
							   name="problemdescription" value=""></td>
				</tr>
				<tr id="Itemstartdate">
					<td><bean:message key="oscarEncounter.startdate.title" />:</td>
					<td><input type="text" id="startdate" name="startdate"
							   value="" size="12"> (YYYY-MM-DD)</td>
				</tr>
				<tr id="Itemresolutiondate">
					<td><bean:message key="oscarEncounter.resolutionDate.title" />:
					</td>
					<td><input type="text" id="resolutiondate"
							   name="resolutiondate" value="" size="12"> (YYYY-MM-DD)</td>
				</tr>
				<tr id="Itemageatonset">
					<td><bean:message key="oscarEncounter.ageAtOnset.title" />:</td>
					<td><input type="text" id="ageatonset" name="ageatonset"
							   value="" size="2"></td>
				</tr>

				<tr id="Itemproceduredate">
					<td><bean:message key="oscarEncounter.procedureDate.title" />:
					</td>
					<td><input type="text" id="proceduredate" name="proceduredate"
							   value="" size="12"> (YYYY-MM-DD)</td>
				</tr>
				<tr id="Itemtreatment">
					<td><bean:message key="oscarEncounter.treatment.title" />:</td>
					<td><input type="text" id="treatment" name="treatment"
							   value=""></td>
				</tr>
				<tr id="Itemproblemstatus">
					<td><bean:message key="oscarEncounter.problemStatus.title" />:
					</td>
					<td><input type="text" id="problemstatus" name="problemstatus"
							   value="" size="8"> <bean:message
							key="oscarEncounter.problemStatusExample.msg" /></td>
				</tr>
				<tr id="Itemexposuredetail">
					<td><bean:message key="oscarEncounter.exposureDetail.title" />:
					</td>
					<td><input type="text" id="exposuredetail"
							   name="exposuredetail" value=""></td>
				</tr>
				<tr id="Itemrelationship">
					<td><bean:message key="oscarEncounter.relationship.title" />:
					</td>
					<td><input type="text" id="relationship" name="relationship"
							   value=""></td>
				</tr>
				<tr id="Itemlifestage">
					<td><bean:message key="oscarEncounter.lifestage.title" />:</td>
					<td><select name="lifestage" id="lifestage">
						<option value="">
							<bean:message key="oscarEncounter.lifestage.opt.notset" />
						</option>
						<option value="N">
							<bean:message key="oscarEncounter.lifestage.opt.newborn" />
						</option>
						<option value="I">
							<bean:message key="oscarEncounter.lifestage.opt.infant" />
						</option>
						<option value="C">
							<bean:message key="oscarEncounter.lifestage.opt.child" />
						</option>
						<option value="T">
							<bean:message key="oscarEncounter.lifestage.opt.adolescent" />
						</option>
						<option value="A">
							<bean:message key="oscarEncounter.lifestage.opt.adult" />
						</option>
					</select></td>
				</tr>
				<tr id="Itemhidecpp">
					<td><bean:message key="oscarEncounter.hidecpp.title" />:</td>
					<td><select id="hidecpp" name="hidecpp">
						<option value="0">No</option>
						<option value="1">Yes</option>
					</select></td>
				</tr>
			</table>
			<br> <span style="float: right; margin-right: 10px;"> <input
				type="image"
				src="<c:out value="${ctx}/oscarEncounter/graphics/copy.png"/>"
				title='<bean:message key="oscarEncounter.Index.btnCopy"/>'
				onclick="copyCppToCurrentNote(); return false;"> <input
				type="image"
				src="<c:out value="${ctx}/oscarEncounter/graphics/annotation.png"/>"
				title='<bean:message key="oscarEncounter.Index.btnAnnotation"/>'
				id="anno" style="padding-right: 10px;"> <input type="image"
															   src="<c:out value="${ctx}/oscarEncounter/graphics/edit-cut.png"/>"
															   title='<bean:message key="oscarEncounter.Index.btnArchive"/>'
															   onclick="$('archived').value='true';" style="padding-right: 10px;">
				<input type="image"
					   src="<c:out value="${ctx}/oscarEncounter/graphics/note-save.png"/>"
					   title='<bean:message key="oscarEncounter.Index.btnSignSave"/>'
					   onclick="$('archived').value='false';" style="padding-right: 10px;">
				<input type="image"
					   src="<c:out value="${ctx}/oscarEncounter/graphics/system-log-out.png"/>"
					   title='<bean:message key="global.btnExit"/>'
					   onclick="this.focus();$('channel').style.visibility ='visible';$('showEditNote').style.display='none';return false;">
			</span>
			<bean:message key="oscarEncounter.Index.btnPosition" />
			<select id="position" name="position"><option id="popt0"
														  value="0">1</option>
			</select>
			<div id="issueNoteInfo" style="clear: both; text-align: left;"></div>
			<div id="issueListCPP"
				 style="background-color: #FFFFFF; height: 200px; width: 350px; position: absolute; z-index: 1; display: none; overflow: auto;">
				<div class="enTemplate_name_auto_complete"
					 id="issueAutocompleteListCPP"
					 style="position: relative; left: 0px; display: none;"></div>
			</div>
			<bean:message key="oscarEncounter.Index.assnIssue" />
			&nbsp;<input tabindex="100" type="text" id="issueAutocompleteCPP"
						 name="issueSearch" style="z-index: 2;" size="25">&nbsp; <span
				id="busy2" style="display: none"><img
				style="position: absolute;"
				src="<c:out value="${ctx}/oscarEncounter/graphics/busy.gif"/>"
				alt="<bean:message key="oscarEncounter.Index.btnWorking"/>"></span>

		</form>
	</div>
	<div id="printOps" class="printOps">
		<h3 style="margin-bottom: 5px; text-align: center;">
			<bean:message key="oscarEncounter.Index.PrintDialog" />
		</h3>
		<form id="frmPrintOps" action="" onsubmit="return false;">
			<table id="printElementsTable">
				<tr>
					<td><input type="radio" id="printopSelected" name="printop"
							   value="selected">
						<bean:message key="oscarEncounter.Index.PrintSelect" /></td>
					<td>
						<%
							String roleName = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
						%> <security:oscarSec roleName="<%=roleName%>"
											  objectName="_newCasemgmt.cpp" rights="r" reverse="false">
						<img style="cursor: pointer;"
							 title="<bean:message key="oscarEncounter.print.title"/>"
							 id='imgPrintCPP'
							 alt="<bean:message key="oscarEncounter.togglePrintCPP.title"/>"
							 onclick="return printInfo(this,'printCPP');"
							 src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
							key="oscarEncounter.cpp.title" />
					</security:oscarSec>
					</td>
				</tr>
				<tr>
					<td><input type="radio" id="printopAll" name="printop"
							   value="all">
						<bean:message key="oscarEncounter.Index.PrintAll" /></td>
					<td><img style="cursor: pointer;"
							 title="<bean:message key="oscarEncounter.print.title"/>"
							 id='imgPrintRx'
							 alt="<bean:message key="oscarEncounter.togglePrintRx.title"/>"
							 onclick="return printInfo(this, 'printRx');"
							 src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
							key="oscarEncounter.Rx.title" /></td>
				</tr>
				<tr>
					<td></td>
					<td><img style="cursor: pointer;"
							 title="<bean:message key="oscarEncounter.print.title"/>"
							 id='imgPrintLabs'
							 alt="<bean:message key="oscarEncounter.togglePrintLabs.title"/>"
							 onclick="return printInfo(this, 'printLabs');"
							 src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
							key="oscarEncounter.Labs.title" /></td>
				</tr>
				<!--  extension point -->
				<tr id="printDateRow">
					<td><input type="radio" id="printopDates" name="printop"
							   value="dates">
						<bean:message key="oscarEncounter.Index.PrintDates" />&nbsp;<a
								style="font-variant: small-caps;" href="#"
								onclick="return printToday(event);"><bean:message
								key="oscarEncounter.Index.PrintToday" /></a></td>
					<td></td>
				</tr>
			</table>

			<div style="float: left; margin-left: 5px; width: 30px;">
				<bean:message key="oscarEncounter.Index.PrintFrom" />
				:
			</div>
			<img src="<c:out value="${ctx}/images/cal.gif" />"
				 id="printStartDate_cal" alt="calendar">&nbsp;<input
				type="text" id="printStartDate" name="printStartDate"
				ondblclick="this.value='';"
				style="font-style: italic; border: 1px solid #7682b1; width: 125px; background-color: #FFFFFF;"
				readonly value=""><br>
			<div style="float: left; margin-left: 5px; width: 30px;">
				<bean:message key="oscarEncounter.Index.PrintTo" />
				:
			</div>
			<img src="<c:out value="${ctx}/images/cal.gif" />"
				 id="printEndDate_cal" alt="calendar">&nbsp;<input type="text"
																   id="printEndDate" name="printEndDate" ondblclick="this.value='';"
																   style="font-style: italic; border: 1px solid #7682b1; width: 125px; background-color: #FFFFFF;"
																   readonly value=""><br>
			<div style="margin-top: 5px; text-align: center">
				<input type="submit" id="printOp" style="border: 1px solid #7682b1;"
					   value="Print" onclick="return printNotes();">

				<indivo:indivoRegistered
						demographic="<%=(String) request.getAttribute(\"demographicNo\")%>"
						provider="<%=(String) request.getSession().getAttribute(\"user\")%>">
					<input type="submit" id="sendToPhr"
						   style="border: 1px solid #7682b1;" value="Send To Phr"
						   onclick="return sendToPhrr();">
				</indivo:indivoRegistered>
				<input type="submit" id="cancelprintOp"
					   style="border: 1px solid #7682b1;" value="Cancel"
					   onclick="$('printOps').style.display='none';"> <input
					type="submit" id="clearprintOp" style="border: 1px solid #7682b1;"
					value="Clear"
					onclick="$('printOps').style.display='none'; return clearAll(event);">
			</div>

			<%
				if (OscarProperties.getInstance().getBooleanProperty("note_program_ui_enabled", "true")) {
			%>
			<span class="popup" style="display: none;" id="_program_popup">
				<div class="arrow"></div>
				<div class="contents">
					<div class="selects">
						<select class="selectProgram"></select> <select class="role"></select>
					</div>
					<div class="under">
						<div class="errorMessage"></div>
						<input type="button" class="scopeBtn" value="View Note Scope" />
						<input type="button" class="closeBtn" value="Close" /> <input
							type="button" class="saveBtn" value="Save" />
					</div>
				</div>
			</span>

			<div id="_program_scope" class="_program_screen"
				 style="display: none;">
				<div class="_scopeBox">
					<div class="boxTitle">
						<span class="text">Note Permission Summary</span><span
							class="uiBigBarBtn"><span class="text">x</span></span>
					</div>
					<table class="details">
						<tr>
							<th>Program Name (of this note)</th>
							<td class="programName">...</td>
						</tr>
						<tr>
							<th>Role Name (of this note)</th>
							<td class="roleName">...</td>
						</tr>
					</table>
					<div class="explanation">The following is a summary of what
						kind of access providers in the above program have to this note.</div>
					<div class="loading">Loading...</div>
					<table class="permissions"></table>
				</div>
			</div>
			<%
				}
			%>
		</form>
	</div>
	<%
		String apptNo = request.getParameter("appointmentNo");
		if( OscarProperties.getInstance().getProperty("resident_review", "false").equalsIgnoreCase("true") &&
				loggedInInfo.getLoggedInProvider().getProviderType().equals("resident") && !"null".equalsIgnoreCase(apptNo) && !"".equalsIgnoreCase(apptNo)) {
			ProviderDataDao providerDao = SpringUtils.getBean(ProviderDataDao.class);
			List<ProviderData> providerList = providerDao.findAllBilling("1");
	%>
	<div id="showResident" class="showResident">

		<div class="showResidentBorder residentText">
			Resident Check List

			<form action="" id="resident" name="resident" onsubmit="return false;">
				<input type="hidden" name="residentMethod" id="residentMethod" value="">
				<input type="hidden" name="residentChain" id="residentChain" value="">
				<table class="showResidentContent">
					<tr>
						<td>
							Was this encounter reviewed?
						</td>
						<td>
							Yes <input type="radio" value="true" name="reviewed">&nbsp;No <input type="radio" value="false" name="reviewed">
						</td>
					</tr>
					<tr class="reviewer" style="display:none">
						<td class="residentText">
							Who did you review the encounter with?
						</td>
						<td>
							<select id="reviewer" name="reviewer">
								<option value="">Choose Reviewer</option>
								<%
									for( ProviderData p : providerList ) {
								%>
								<option value="<%=p.getId()%>"><%=p.getLastName() + ", " + p.getFirstName()%></option>
								<%
									}
								%>
							</select>
						</td>
					</tr>
					<tr class="supervisor" style="display:none">
						<td class="residentText">
							Who is your Supervisor/Monitor for this encounter?
						</td>
						<td>
							<select id="supervisor" name="supervisor">
								<option value="">Choose Supervisor</option>
								<%
									for( ProviderData p : providerList ) {
								%>
								<option value="<%=p.getId()%>"><%=p.getLastName() + ", " + p.getFirstName()%></option>
								<%
									}
								%>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<input id="submitResident" value="Continue" name="submitResident" type="submit" onclick="return subResident();"/>
							<input id="submitResident" value="Return to Chart" name="submitResident" type="submit" onclick="return cancelResident();"/>
						</td>
					</tr>
				</table>
			</form>
		</div>

	</div>
	<%}%>
	</body>
</html:html>
