$.ajax({
    type: "GET",
    url: "/viewCart",
    success: (product) => {
        console.log(product);
        if (product != "") {
            let k = product.split("&");
            for (let i = 0; i < k.length - 1; i++) {
                let arr = k[i].split(" ");
                let id = arr[0];
                let title = arr[1];
                let price = arr[2];
                $("#ccccc").append(`<li>
                        <a href="#" class="photo"><img src="/getProductImg/${id}" class="cart-thumb" alt=""/></a>
                        <h6><p>${title} </p></h6>
                        <p>${price}</p><span class="price">USD</span>
                    </li>`)
            }
            $("#ccccc").append(` <li class="total">
                        <a href="/cart" class="btn btn-default hvr-hover btn-cart">VIEW CART</a>
                    </li>`)
        }
    }
})
