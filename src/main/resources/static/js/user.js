$(".cats").click(function(){
    let catId = this.dataset.id;
    $.ajax({
        url: "/getProductByCat",
        data:{
            catId: catId
        },
        type:"GET",
        success:function (str) {
            $("#cats").html("");
            let arr = str.split("\n");
            console.log(arr);
            for (let i = 0;i<arr.length-1;i++){
                let sub_arr=arr[i].split("&copy;");
                let title = sub_arr[0];
                let price = sub_arr[1];
                let id = sub_arr[2];
                let color = sub_arr[3];

                $("#cats").append(`<div class="col-lg-3 col-md-6 special-grid" id="${id}">
                <div class="products-single fix">
                    <div class="box-img-hover" style="height: 250px;">
<!--                        <div class="type-lb">-->
<!--                            <p class="sale">Sale</p>-->
<!--                        </div>-->
                        <img src="/getProductImg/${id}" class="img-fluid" alt="Image" >
                        <div class="mask-icon">
                            <ul>
                                <li><a href="/view/${id}" data-toggle="tooltip" data-placement="right" title="View"><i class="fas fa-eye"></i></a></li>
                                <li><a href="/editPr/${id}" data-toggle="tooltip" data-placement="right" title="Compare"><i class="fas fa-sync-alt"></i></a></li>
                              
                            </ul>
                            <button class="cart delete" onclick="del(this)" id="${id}">Delete</button>
                        </div>
                    </div>
                    <div class="why-text" style="background: ${color}">
                        <h4 style="color: white">${title}</h4>
                        <h5 style="background: ${color};color: white">${price} AMD</h5>
                    </div>
                </div>
            </div>`)

            }
        }
    })
})


$("#all").click(function(){
    $.ajax({
        url: "/allProduct",
        type:"POST",
        success: function (p) {
            $("#cats").html("");
            let arr = p.split("\n");
            console.log(arr)
            for (let i =0;i<arr.length-1;i++){
                let sub_arr=arr[i].split("&copy;");
                let title = sub_arr[0];
                let price = sub_arr[1];
                let id = sub_arr[2];
                let color = sub_arr[3];
                $("#cats").append(`<div class="col-lg-3 col-md-6 special-grid" id="${id}" >
                <div class="products-single fix">
                    <div class="box-img-hover" style="height: 250px;">
<!--                        <div class="type-lb">-->
<!--                            <p class="sale">Sale</p>-->
<!--                        </div>-->
                        <img src="/getProductImg/${id}" class="img-fluid" alt="Image">
                        <div class="mask-icon">
                            <ul>
                                <li><a href="/view/${id}" data-toggle="tooltip" data-placement="right" title="View"><i class="fas fa-eye"></i></a></li>
                                <li><a href="/editPr/${id}" data-toggle="tooltip" data-placement="right" title="Compare"><i class="fas fa-sync-alt"></i></a></li>
                             
                            </ul>
                            <button class="cart delete" onclick="del(this)" id="${id}">Delete</button>
                        </div>
                    </div>
                    <div class="why-text" style="background: ${color}">
                        <h4 style="color: white">${title}</h4>
                        <h5 style="background: ${color};color: white">${price} AMD</h5>
                    </div>
                </div>
            </div>`)
            }
            console.log("xxxxxx")
        }
    })
})


$(".delete").click(function(){
    let productId = this.dataset.id;
    $.ajax({
        url: "/deleteProduct",
        type:"POST",
        data: {
            productId: productId
        }, success: function (id) {
             document.getElementById(id).remove();
        }
    })
})

function del(obj) {
    let productId =obj.id;
    console.log(productId);
    $.ajax({
        type: "POST",
        url: "/deleteProduct",
        data: {
            productId: productId
        }, success: function (id) {
            document.getElementById(id).remove();


        }
    })
}


//
// var current_page = 1;
// var records_per_page = 2;
//
// var objJson = document.getElementById("listingTable")
//
// function prevPage()
// {
//     if (current_page > 1) {
//         current_page--;
//         changePage(current_page);
//     }
// }
//
// function nextPage()
// {
//     if (current_page < numPages()) {
//         current_page++;
//         changePage(current_page);
//     }
// }
//
// function changePage(page)
// {
//     var btn_next = document.getElementById("btn_next");
//     var btn_prev = document.getElementById("btn_prev");
//     var listing_table = document.getElementById("listingTable");
//     var page_span = document.getElementById("page");
//
//     // Validate page
//     if (page < 1) page = 1;
//     if (page > numPages()) page = numPages();
//
//     listing_table.innerHTML = "";
//
//     for (var i = (page-1) * records_per_page; i < (page * records_per_page); i++) {
//         listing_table.innerHTML += objJson[i].adName + "<br>";
//     }
//     page_span.innerHTML = page;
//
//     if (page == 1) {
//         btn_prev.style.visibility = "hidden";
//     } else {
//         btn_prev.style.visibility = "visible";
//     }
//
//     if (page == numPages()) {
//         btn_next.style.visibility = "hidden";
//     } else {
//         btn_next.style.visibility = "visible";
//     }
// }
//
// function numPages()
// {
//     return 3;
// }
//
// window.onload = function() {
//     changePage(1);
// };








