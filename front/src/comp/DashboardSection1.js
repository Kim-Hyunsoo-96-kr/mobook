import {axiosInstance, CONFIG, isLoginedSelector, setLogin, Toast} from "../recoil";
import {useEffect, useState} from "react";
import axios from "axios";
import Swal from "sweetalert2";
import {Link} from "react-router-dom";

const  DashboardSection1 = () => {
    const [dashboard, setDashboard] = useState(null);

    useEffect(() => {
        const getRecentBookListTop5 = async () => {
            try {
                const response = await axios.get(CONFIG.API_DASHBOARD);
                setDashboard(response.data);
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

    if(dashboard === null){
        return <div>로딩중...</div>;
    }
    return (
        <section>
            <div className="py-5 bg-light">
            <div className="container px-5 my-5">
                <div className="row row-cols-1 row-cols-md-3 mb-3 text-center">
                    <div className="accordion" id="accordionPanelsStayOpenExample">
                        <div className="accordion-item">
                            <h2 className="accordion-header" id="panelsStayOpen-headingOne">
                                <button className="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseOne" aria-expanded="false" aria-controls="panelsStayOpen-collapseOne">
                                    최근 추가된 책
                                </button>
                            </h2>
                            <div id="panelsStayOpen-collapseOne" className="accordion-collapse collapse show" aria-labelledby="panelsStayOpen-headingOne">
                                <div className="accordion-body">
                                    <div className="col">
                                        <div className="card mb-4 rounded-3 shadow-sm">
                                            <div className="card-body">
                                                <ul className="list-unstyled mt-3 mb-4 text-left">
                                                    {dashboard.recentBookList.map((recentBook, index)=>(
                                                        <li className="ellipsis mb-2">{index+1}.  {recentBook.bookName}</li>
                                                    ))}
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="col">
                        <div className="accordion" id="accordionPanelsStayOpenExample">
                            <div className="accordion-item">
                                <h2 className="accordion-header" id="panelsStayOpen-headingTwo">
                                    <button className="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseTwo" aria-expanded="false" aria-controls="panelsStayOpen-collapseTwo">
                                        인기책
                                    </button>
                                </h2>
                                <div id="panelsStayOpen-collapseTwo" className="accordion-collapse collapse show" aria-labelledby="panelsStayOpen-headingTwo">
                                    <div className="accordion-body">
                                        <div className="col">
                                            <div className="card mb-4 rounded-3 shadow-sm">
                                                <div className="card-body">
                                                    <ul className="list-unstyled mt-3 mb-4 text-left">
                                                        {dashboard.popularBookList.map((popularBook, index)=>(
                                                            <li className="ellipsis mb-2">{index+1}.  {popularBook.bookName}</li>
                                                        ))}
                                                    </ul>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="col">
                        <div className="accordion" id="accordionPanelsStayOpenExample">
                            <div className="accordion-item">
                                <h2 className="accordion-header" id="panelsStayOpen-headingThree">
                                    <button className="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseThree" aria-expanded="false" aria-controls="panelsStayOpen-collapseTwo">
                                        대여 현황
                                    </button>
                                </h2>
                                <div id="panelsStayOpen-collapseThree" className="accordion-collapse collapse show" aria-labelledby="panelsStayOpen-headingThree">
                                    <div className="accordion-body">
                                        <div className="col">
                                            <div className="card mb-4 rounded-3 shadow-sm">
                                                <div className="card-body">
                                                    <ul className="list-unstyled mt-3 mb-4 text-left">
                                                        {dashboard.rentBookList.map((rentBook, index)=>(
                                                            <li className="ellipsis mb-2">{index+1}.  {rentBook.userName} - {rentBook.bookName}</li>
                                                        ))}
                                                    </ul>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
        </section>
    );
}

export default DashboardSection1;
