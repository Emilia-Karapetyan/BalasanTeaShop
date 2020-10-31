

let inpQuantity = document.querySelectorAll(".quantity");
let pTotal = document.querySelectorAll(".totalll");
let sum = 0;
for (let i = 0; i < inpQuantity.length; i++) {
    inpQuantity[i].addEventListener("input", getTotal);
    $.ajax({
        type: "GET",
        url: "/getQuantity",
        data: {
            val: inpQuantity[i].value,
            id: inpQuantity[i].id
        }, success: function (total) {
            sum = 0;
            for (let j = 0; j < pTotal.length; j++) {
                if (pTotal[j].id === inpQuantity[i].id) {
                    pTotal[j].innerHTML = total;
                }
                sum += +pTotal[j].innerHTML;
            }
        }
    })
}

function getTotal() {
    $.ajax({
        type: "GET",
        url: "/getQuantity",
        data: {
            val: this.value,
            id: this.id
        }, success: (total) => {
            sum = 0;
            for (let i = 0; i < pTotal.length; i++) {
                if (pTotal[i].id === this.id) {
                    pTotal[i].innerHTML = total;
                }
                sum += +pTotal[i].innerHTML;
            }
            let i = document.getElementById("scr");
            let inp = document.getElementById("amount");
            let s = sum;
            i.dataset.amount = s;
            $(".allPrice").text(sum);
            inp.value = sum * 100;
        }
    })
}




$(".frCart").click(function () {
    let id = this.id;
    $.ajax({
        type: "GET",
        url: "/deleteCart",
        data: {
            id: id
        }, success: function () {
            let c=document.getElementById(id).parentElement.parentElement.children[4].children[0].innerHTML;
            sum=sum-c;
            document.getElementById(id).parentElement.parentElement.remove();
            console.log(sum);
            let i = document.getElementById("scr");
            let inp = document.getElementById("amount");
            let s = sum;
            i.dataset.amount = s;
            $(".allPrice").text(sum);
            inp.value = sum * 100;
        }
    })
})