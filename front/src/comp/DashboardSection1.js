import {axiosInstance, CONFIG, isLoginedSelector, setLogin, Toast} from "../recoil";
import {useEffect, useState} from "react";
import axios from "axios";
import Swal from "sweetalert2";

const  DashboardSection1 = () => {
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
        <section className="py-5 bg-light">
            <div className="container px-5 my-5">
                <div className="row row-cols-1 row-cols-md-3 mb-3 text-center">
                        <div className="col">
                            <div className="card mb-4 rounded-3 shadow-sm">
                                <div className="card-header py-3">
                                    <h4 className="my-0 fw-normal">최신 등록 책</h4>
                            </div>
                            <div className="card-body">
                                <ul className="list-unstyled mt-3 mb-4">
                                    {recentBookList.bookList.map((book)=>(
                                        <li>{book.bookName}</li>
                                    ))}
                                </ul>
                            <button type="button" className="w-100 btn btn-outline-primary">책 목록으로 가기</button>
                        </div>
                    </div>
                </div>
                        <div className="col">
                            <div className="card mb-4 rounded-3 shadow-sm">
                                <div className="card-header py-3">
                                    <h4 className="my-0 fw-normal">인기 책</h4>
                        </div>
                                <div className="card-body">
                                    <ul className="list-unstyled mt-3 mb-4">
                                        <li>인기 책 - 1</li>
                                        <li>인기 책 - 2</li>
                                        <li>인기 책 - 3</li>
                                        <li>인기 책 - 4</li>
                            </ul>
                            <button type="button" className="w-100 btn btn-lg btn-primary">Get started</button>
                        </div>
                    </div>
                </div>
                        <div className="col">
                            <div className="card mb-4 rounded-3 shadow-sm border-primary">
                                <div className="card-header py-3 text-bg-primary border-primary">
                                    <h4 className="my-0 fw-normal">대여 현황</h4>
                        </div>
                        <div class="card-body">
                            <ul class="list-unstyled mt-3 mb-4">
                                <li>대여 현황 - 1</li>
                                <li>대여 현황 - 2</li>
                                <li>대여 현황 - 3</li>
                                <li>대여 현황 - 4</li>
                            </ul>
                            <button type="button" class="w-100 btn btn-lg btn-primary">Contact us</button>
                        </div>
                    </div>
                </div>
            </div>
            </div>
        </section>
    );
}

export default DashboardSection1;
