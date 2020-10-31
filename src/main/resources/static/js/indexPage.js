
$(".cartssss").click(function () {
    let id=this.dataset.id;
    $.ajax({
        type:"GET",
        url:"/addToCart",
        data:{
            id:id
        },success:addCart=>{
            if(addCart==="NotFound")
                alert("Please sign in");
            else {
                this.setAttribute("disabled", true)
                this.style.background="gray";
            }
        }
    })
})