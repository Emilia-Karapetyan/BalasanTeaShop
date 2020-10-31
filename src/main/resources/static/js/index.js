let btnLike=document.querySelectorAll(".like");
for(let i=0;i<btnLike.length;i++){
    btnLike[i].addEventListener("click",addWishList);
}
function addWishList() {
    let id=this.dataset.id;
    $.ajax({
        type:"GET",
        url:"/addWishList",
        data:{
            id:id
        },success: (isLike)=> {
            if (isLike==="NotFound"){
                alert("Please Sign In");
            }else if(isLike==="Wish"){
                alert("It is Already in cart");
            }
            else {
                let i = document.getElementById(id);
                console.log(this.children[0])
                if (this.children[0].className === "fa fa-heart") {
                    console.log(1)
                    this.children[0].className = "fa fa-heart-o";
                } else {
                    console.log(2)
                    this.children[0].className = "fa fa-heart";
                }
            }
        }
    })
}
