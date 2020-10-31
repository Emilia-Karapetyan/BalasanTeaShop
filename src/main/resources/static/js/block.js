let bool = true;
$(".ll").click(function () {
    let idd = this.dataset.id;
    let x = this.parentElement.parentElement.children[6].children[0].value;
    if (x !== "") {
        bool = true;
        $.ajax({
            type: "GET",
            url: "/blockUser",
            data: {
                id: idd,
                sec: x * 60
            }, success: () => {
                this.parentElement.parentElement.children[6].children[0].remove();
                let p = document.createElement("p");
                p.className = idd;
                p.innerHTML = x * 60;
                this.parentElement.parentElement.children[6].append(p);
                let btn = document.createElement("button");
                btn.textContent = "Stop";
                btn.className = "btn btn-danger";
                btn.type = "button";
                btn.dataset.id = idd;
                btn.addEventListener("click", stopTimer)
                this.parentElement.append(btn);
                this.remove();
            }
        })
    }
})

$(".stop").click(function () {
    let idd = this.dataset.id;
    console.log(idd);
    $.ajax({
        type: "GET",
        url: "/stopTimer",
        data: {
            id: idd,
            sec: 0
        }, success: (end) => {
            if (end === "end") {
                bool = false;
            }
            feedBack(this,idd);
            // this.parentElement.parentElement.children[6].children[0].remove();
            // let input = document.createElement("input");
            // input.className = "time";
            // input.type = "number";
            // input.id = "userBlock";
            // input.min = "0";
            // input.dataset.id = idd;
            // this.parentElement.parentElement.children[6].append(input);
            // let btn = document.createElement("button");
            // btn.textContent = "Block";
            // btn.dataset.id = idd;
            // btn.type = "button";
            // btn.className = "btn btn-info";
            // btn.dataset.id = idd;
            // btn.addEventListener("click", start);
            // this.parentElement.append(btn);
            // this.remove();
        }
    })
})

function start() {
    console.log("xx")
    let idd = this.dataset.id;
    let x = this.parentElement.parentElement.children[6].children[0].value;
    if (x !== "") {
        bool = true;
        $.ajax({
            type: "GET",
            url: "/blockUser",
            data: {
                id: idd,
                sec: x * 60
            }, success: () => {
                this.parentElement.parentElement.children[6].children[0].remove();
                let p = document.createElement("p");
                p.className = idd;
                p.innerHTML = x * 60;
                this.parentElement.parentElement.children[6].append(p);
                let btn = document.createElement("button");
                btn.textContent = "Stop";
                btn.className = "btn btn-danger";
                btn.type = "button";
                btn.dataset.id = idd;
                btn.addEventListener("click", stopTimer)
                this.parentElement.append(btn);
                this.remove();
            }
        })
    }
}
function stopTimer() {
    let idd = this.dataset.id;
    $.ajax({
        type: "GET",
        url: "/stopTimer",
        data: {
            id: idd,
            sec: 0
        }, success: (end) => {
            if (end === "end") {
                bool = false;
            }
            feedBack(this,idd);
            // this.parentElement.parentElement.children[6].children[0].remove();
            // let input = document.createElement("input");
            // input.className = "time";
            // input.type = "number";
            // input.id = "userBlock";
            // input.min = "0";
            // input.dataset.id = idd;
            // this.parentElement.parentElement.children[6].append(input);
            // let btn = document.createElement("button");
            // btn.textContent = "Block";
            // btn.dataset.id = idd;
            // btn.type = "button";
            // btn.className = "btn btn-info";
            // btn.dataset.id = idd;
            // btn.addEventListener("click", start)
            // this.parentElement.append(btn);
            // this.remove();
        }
    })
}

setInterval(() => {
    if (bool) {
        $.ajax({
            type: "GET",
            url: "/blockUserSec",
            data: {},
            success: (list) => {
                if (list.length < 4) {
                    bool = false;
                } else {
                    if (list.length > 5) {
                        let arr = list.split(",");
                        for (let i = 0; i < arr.length - 1; i++) {
                            let x = arr[i].split("=")
                            let id = x[0];
                            let p = document.getElementsByClassName(id);
                            let sec = x[1];
                            if(sec.includes(",")){
                                sec=arr[1].substring(0, arr.length-1)
                            }
                            if(sec==1){
                                let input = document.createElement("input");
                                input.className = "time";
                                input.type = "number";
                                input.id = "userBlock";
                                input.min = "0";
                                input.dataset.id = id;
                                p[0].parentElement.parentElement.children[6].append(input);
                                let btn = document.createElement("button");
                                btn.textContent = "Block";
                                btn.dataset.id = id;
                                btn.type = "button";
                                btn.className = "btn btn-info";
                                btn.addEventListener("click", start)
                                p[0].parentElement.parentElement.children[7].children[0].remove();
                                p[0].parentElement.parentElement.children[7].append(btn);
                                p[0].remove();
                            }else {
                                p[0].innerHTML = sec;
                            }
                        }
                    } else {
                        let arr = list.split("=");
                        let id = arr[0];
                        let p = document.getElementsByClassName(id);
                        let sec = arr[1].substring(0, arr.length);
                        if(sec.includes(",")){
                            sec=arr[1].substring(0, arr.length-1)
                        }
                        if(sec==1){
                            let input = document.createElement("input");
                            input.className = "time";
                            input.type = "number";
                            input.id = "userBlock";
                            input.min = "0";
                            input.dataset.id = id;
                            p[0].parentElement.parentElement.children[6].append(input);
                            let btn = document.createElement("button");
                            btn.textContent = "Block";
                            btn.dataset.id = id;
                            btn.type = "button";
                            btn.className = "btn btn-info";
                            btn.addEventListener("click", start)
                            p[0].parentElement.parentElement.children[7].children[0].remove();
                            p[0].parentElement.parentElement.children[7].append(btn);
                            p[0].remove();
                        }else {
                            p[0].innerHTML = sec;
                        }
                    }
                }
            }
        })
    }
}, 1000);

function feedBack(obj,i) {
    obj.parentElement.parentElement.children[6].children[0].remove();
    let input = document.createElement("input");
    input.className = "time";
    input.type = "number";
    input.id = "userBlock";
    input.min = "0";
    input.dataset.id = i;
    obj.parentElement.parentElement.children[6].append(input);
    let btn = document.createElement("button");
    btn.textContent = "Block";
    btn.dataset.id = i;
    btn.type = "button";
    btn.className = "btn btn-info";
    btn.addEventListener("click", start)
    obj.parentElement.append(btn);
    obj.remove();
}