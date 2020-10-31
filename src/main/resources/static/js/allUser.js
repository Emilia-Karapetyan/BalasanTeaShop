$(".del").click(function(){
    let userId = this.dataset.id;
    $.ajax({
        type: "GET",
        url:"/deleteUser",
        data:{
            userId:userId
        },success:function(){
            console.log(userId)
            document.getElementById(userId).remove();
        }
    })
})