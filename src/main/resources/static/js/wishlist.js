$(".del").click(function(){
    let prId = this.dataset.id;
    $.ajax({
        type: "GET",
        url:"/deletePrWishlist",
        data:{
            prId:prId
        },success:function(){
            document.getElementById(prId).remove();
        }
    })
})
