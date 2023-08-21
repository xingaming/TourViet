
                             


                                $(document).ready(function () {
        const tabs = document.querySelectorAll('.tab_btn');
        const all_content = document.querySelectorAll('.content');
        tabs.forEach((tab, index) => {
            tab.addEventListener('click',() =>{
                tab.classList.add('active');
            })
        });
                                });
