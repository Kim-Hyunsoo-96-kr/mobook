import {axiosInstance, CONFIG, isLoginedSelector, setLogin, Toast} from "../recoil";
import {useEffect, useState} from "react";
import axios from "axios";
import Swal from "sweetalert2";

const  DashboardSection2 = () => {
    const [recentBookList, setRecentBookList] = useState(null);

    useEffect(() => {
        const getRecentBookListTop5 = async () => {
            try {
                const response = await axios.get(CONFIG.API_RECENTBOOKTOP5);
                setRecentBookList(response.data);
            } catch (e){
                Swal.fire(
                    e.response.data.message,
                    '송주환 사원에게 문의해주세요.',
                    'warning'
                )
            }
        }
        getRecentBookListTop5();
    }, []);

    if(recentBookList === null){
        return <div>로딩중...</div>;
    }
    return (
        <section>
            <div className="container px-5 py-5 width80per">
                <div className="accordion" id="accordionPanelsStayOpenExample">
                    <div className="accordion-item">
                        <h2 className="accordion-header" id="panelsStayOpen-headingOne">
                            <button className="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseOne" aria-expanded="true" aria-controls="panelsStayOpen-collapseOne">
                                최근 추가된 책
                            </button>
                        </h2>
                        <div id="panelsStayOpen-collapseOne" className="accordion-collapse collapse show" aria-labelledby="panelsStayOpen-headingOne">
                            <div className="accordion-body">
                                <div className="col">
                                    <div className="card mb-4 rounded-3 shadow-sm">
                                        <div className="card-body">
                                            <ul className="list-unstyled mt-3 mb-4 text-left">
                                                {recentBookList.bookList.map((book, index)=>(
                                                    <li className="ellipsis">{index+1}.  {book.bookName}</li>
                                                ))}
                                            </ul>
                                            <button type="button" className="w-100 btn btn-outline-primary">책 목록으로 가기</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="accordion-item">
                        <h2 className="accordion-header" id="panelsStayOpen-headingTwo">
                            <button className="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseTwo" aria-expanded="false" aria-controls="panelsStayOpen-collapseTwo">
                                인기 책
                            </button>
                        </h2>
                        <div id="panelsStayOpen-collapseTwo" className="accordion-collapse collapse" aria-labelledby="panelsStayOpen-headingTwo">
                            <div className="accordion-body">
                                <strong>This is the second item's accordion body.</strong> It is hidden by default, until the collapse plugin adds the appropriate classes that we use to style each element. These classes control the overall appearance, as well as the showing and hiding via CSS transitions. You can modify any of this with custom CSS or overriding our default variables. It's also worth noting that just about any HTML can go within the <code>.accordion-body</code>, though the transition does limit overflow.
                            </div>
                        </div>
                    </div>
                    <div className="accordion-item">
                        <h2 className="accordion-header" id="panelsStayOpen-headingThree">
                            <button className="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseThree" aria-expanded="false" aria-controls="panelsStayOpen-collapseThree">
                                대여 현황
                            </button>
                        </h2>
                        <div id="panelsStayOpen-collapseThree" className="accordion-collapse collapse" aria-labelledby="panelsStayOpen-headingThree">
                            <div class="accordion-body">
                                <strong>This is the third item's accordion body.</strong> It is hidden by default, until the collapse plugin adds the appropriate classes that we use to style each element. These classes control the overall appearance, as well as the showing and hiding via CSS transitions. You can modify any of this with custom CSS or overriding our default variables. It's also worth noting that just about any HTML can go within the <code>.accordion-body</code>, though the transition does limit overflow.
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </section>
    );
}

export default DashboardSection2;
