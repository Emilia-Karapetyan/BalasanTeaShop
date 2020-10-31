$(".cartssss").click(function () {
    let id=this.dataset.id;
    $.ajax({
        type:"GET",
        url:"/addToCartWishList",
        data:{
            id:id
        },success:addCart=>{
            document.getElementById(id).remove();
        }
    })
})