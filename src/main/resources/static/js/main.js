// var usernamePage = document.querySelector('#username-page');
// var chatPage = document.querySelector('#chat-page');
// var usernameForm = document.querySelector('#usernameForm');
// var messageForm = document.getElementById('messageForm');
// var messageInput = document.querySelector('#message');
// var messageArea = document.querySelector('#messageArea');
// var connectingElement = document.querySelector('.connecting');
//
// var stompClient = null;
// var username = null;
//
//
// function connect(event) {
//     username = document.querySelector('#name').value.trim();
//
//     console.log("connect");
//     console.log(username);
//     if (username && stompClient==null) {
//         var socket = new SockJS('/javatechie');
//         stompClient = Stomp.over(socket);
//
//         stompClient.connect({}, onConnected, onError);
//     }
//     event.preventDefault();
// }
//
//
// function onConnected() {
//     stompClient.subscribe('/topic/public', onMessageReceived);
//     stompClient.send("/app/chat.register",
//         {},
//         JSON.stringify({sender: username, type: 'JOIN'})
//     )
//     connectingElement.classList.add('hidden');
// }
//
//
// function onError(error) {
//     connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
//     connectingElement.style.color = 'red';
// }
//
//
// function send(event) {
//     var messageContent = messageInput.value.trim();
//     let id =  iddd.dataset.id;
//     let usId = usid.dataset.id;
//     if (messageContent && stompClient) {
//         var chatMessage = {
//             sender: username,
//             content: messageInput.value,
//             prId:id,
//             userId:usId
//         };
//
//         stompClient.send("/app/chat.send", {}, JSON.stringify(chatMessage));
//         messageInput.value = '';
//     }
//     event.preventDefault();
// }
//
//
// function onMessageReceived(payload) {
//     var message = JSON.parse(payload.body);
//     let text = message.content;
//     let pic = picccc.dataset.id;
//     let today = new Date();
//     let date = today.getDate()+'-'+(today.getMonth()+1)+'-'+today.getFullYear();
//     let time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
//     let dateTime =date+' '+time;
//     console.log(message.content)
//     console.log(message)
//     if (text !=null) {
//         $("#messageArea").append(` <div class="media mb-3">
//                         <div class="mr-2">
//                             <img class="rounded-circle border p-1" style="width: 50px"
//                                  src="/image/${pic}"
//                                  alt="Generic placeholder image">
//                         </div>
//                         <div class="media-body">
//                             <p>${text}</p>
//                             <small class="text-muted">${dateTime}</small>
//                         </div>
//                     </div><hr>`)
//     }
// }
// usernameForm.addEventListener('submit', connect, true)
// messageForm.addEventListener('submit', send, true)


var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.getElementById("iddd");
var messageForm = document.getElementById("send");
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');

var stompClient = null;
var username = null;

connect();
function connect() {
    username = document.querySelector('#name').value.trim();
    console.log("connect");
    console.log(username);
    if (username && stompClient == null) {
        var socket = new SockJS('/javatechie');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected);
    }
}


function onConnected() {
    stompClient.subscribe('/topic/public', onMessageReceived);
    stompClient.send("/app/chat.register",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )
}
function send(event) {
    var messageContent = messageInput.value.trim()
    let id = iddd.dataset.id;
    let usId = usss.dataset.id;

    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            prId: id,
            userId: usId
        };

        stompClient.send("/app/chat.send", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    console.log(payload);
    var message = JSON.parse(payload.body);
    if (message.userId !== 0) {
        let text = message.content;
        let usId = message.userId;
        let today = new Date();
        let date = today.getDate() + '-' + (today.getMonth() + 1) + '-' + today.getFullYear();
        let time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
        let dateTime = date + ' ' + time;
        if (text != null) {
            $("#messageArea").append(` <div class="media mb-3">
                        <div class="mr-2">
                            <img class="rounded-circle border p-1"
                                 src="/getImgById/${usId}"
                                 alt="Generic placeholder image" width="50px">
                        </div>
                        <div class="media-body">
                            <p>${text}</p>
                            <small class="text-muted">${dateTime}</small>
                        </div>
                    </div><hr>`)
        }
    }
}
// usernameForm.addEventListener("click",connect);
messageForm.addEventListener("click", send);