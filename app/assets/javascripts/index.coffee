$ ->
  ws = new WebSocket $("body").data("ws-url")
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    switch message.type
      when "newSearch"
        $('#youtube').prepend(message.youtube + "<br/>")
      when "update"
        temp = document.getElementById(message.queryID).innerHTML
        document.getElementById(message.queryID).innerHTML = message.youtube + temp

  $("#search").submit (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({q: $("#q").val()}))
    $("#q").val("")