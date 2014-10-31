/*******************************************************************************
 * @author: eschuhmacher
 *
 * scripts to be included on the html file
 * <script src="../js/widgets/lib/RGraph/libraries/RGraph.common.core.js" ></script>
 * <script type="text/javascript" language="javascript" src="../js/widgets/tank.js"></script>
 * <script src="../js/widgets/lib/RGraph/excanvas/excanvas.js"></script>
 * <script src="../js/widgets/lib/RGraph/libraries/RGraph.vprogress.js" ></script>
 * <script src="../js/widgets/lib/jquery-2.0.3.min.js"></script>
 ******************************************************************************/


$(document).ready(function() {
	var nodes = document.getElementsByClassName("tank");
	var len = nodes.length;
    var tanks = {};
	for ( var i = 0; i < len; i++) {
        var channelname = nodes[i].getAttribute("data-channel");
        var readOnly = nodes[i].getAttribute("channel-readonly");
        var id='rgraph-vprogress-'+i;
        nodes[i].innerHTML = '<canvas id="'+id+'">[No canvas support] </canvas>';
        fitToContainer(nodes[i].firstChild);
        if (channelname != null && channelname.trim().length > 0) {
            var displayLow = nodes[i].getAttribute("displayLow") != null ? parseInt(nodes[i].getAttribute("displayLow")) : 0;
            var displayHigh = nodes[i].getAttribute("displayHigh") != null ? parseInt(nodes[i].getAttribute("displayHigh")) : 100;
            var callback = function(evt, channel) {
                                    switch (evt.type) {
                                    case "connection": //connection state changed
                                        break;
                                    case "value": //value changed
                                        var channelValue = channel.getValue();
                                        if (channelValue.display.lowDisplay == null) {
                                             tanks[channel.getId()] = new RGraph.VProgress(tanks[channel.getId()].id,
                                                                                 displayLow, displayHigh,
                                                                                 channelValue.value);
                                        } else {
                                        tanks[channel.getId()] =new RGraph.VProgress(tanks[channel.getId()].id,
                                                                           channelValue.display.lowDisplay, channelValue.display.highDisplay,
                                                                           channelValue.value);
                                        }
                                        var color = 'green';

                                        if(channelValue.alarm.severity =="MINOR") {
                                          tanks[channel.getId()].Set('chart.colors', ["Gradient(#660:yellow:#660)"]);
                                        } else if (channelValue.alarm.severity =="MAJOR") {
                                           tanks[channel.getId()].Set('chart.colors', ["Gradient(#600:red:#600)"]);
                                        } else {
                                           tanks[channel.getId()].Set('chart.colors', ["Gradient(#060:#0f0:#060)"]);
                                        }
                                        tanks[channel.getId()].Set('chart.scale.visible', true);
                                        tanks[channel.getId()].Set('chart.shadow', false);
                                        tanks[channel.getId()].Draw();
                                        break;
                                    case "error": //error happened
                                        break;
                                    case "writePermission":	// write permission changed.
                                        break;
                                    case "writeCompleted": // write finished.
                                        break;
                                    default:
                                        break;
                                    }
                            };
            var channel = wp.subscribeChannel(channelname, callback, readOnly);
            tanks[channel.getId()] = new RGraph.VProgress(id,displayLow, displayHigh,0);
            tanks[channel.getId()].Set('chart.scale.visible', true);
            tanks[channel.getId()].Set('chart.shadow', false);
            tanks[channel.getId()].Draw();
            tanks[channel.getId()].canvas.onclick = function (e)
            {
                var obj   = e.target.__object__;
                var value = obj.getValue(e);
                obj.value = value;
                for(var sl in   tanks) {
                    if(tanks[sl].id == obj.id) {
                        var ch = wp.getChannel(sl);
                        break;
                    }
                }
                ch.value.value = value;
                ch.updateValue();
            }
        }
    }
});

window.onbeforeunload = function() {
	ws.close();
};

function fitToContainer(canvas){
	  canvas.style.width='100%';
	  canvas.style.height='100%';
	  canvas.width  = canvas.offsetWidth;
	  canvas.height = canvas.offsetHeight;
}
