let searchBody=document.querySelector("#searchBody");
searchBody.classList.remove('in');
console.log(searchBody);
function search () {
    searchBody.innerHTML="";
    console.log($("#inp").val());
    let val=$("#inp").val().trim();
    if(val!=""){
        searchBody.classList.remove('hide');
        searchBody.classList.add('in');
        $.ajax({
            url:"/searchProduct",
            data:{
                str:val
            },success : function (user) {
                let str = user.split("*")
                for (var i = 0; i < str.length; i++) {

                    var s = str[i];
                    let arr = s.split("&");
                    if (arr[0] != undefined && arr[1] != undefined) {
                        let us = document.createElement("a");
                        let img = document.createElement("img");
                        var br = document.createElement("br");
                        us.style.padding = 5;
                        us.style.color = "white";
                        img.style.paddingBottom = 5;
                        console.log(arr[1]);
                        img.src = "/getProductImg/" + arr[1];
                        img.width = 80;
                        console.log(arr)
                        us.href = "/view/"+arr[1];
                        us.innerHTML = arr[0];
                        let p=us.innerText;
                        if(us.innerText.toLowerCase().includes(val.toLowerCase())) {
                            us.innerHTML = insertMark(p, p.toLowerCase().indexOf(val.toLowerCase()), val.length);
                            searchBody.append(img, us, br);
                        }
                    }
                }
            }
        })

    }else{
        searchBody.classList.remove('in');
        searchBody.classList.add('hide');
        searchBody.innerHTML="";
    }
}
function insertMark(str,pos,len) {
    return str.slice(0,pos)+'<mark>'+str.slice(pos,pos+len)+'</mark>'+str.slice(pos+len)
}