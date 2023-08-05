import '../App.css';
import Header from "../comp/Header";
import {useRecoilValue, useSetRecoilState} from "recoil";
import {axiosInstance, CONFIG, isLoginedSelector, loginedUserInfoAtom, loginedUserInfoSelector} from "../recoil";
import {Navigate} from "react-router-dom";
import axios from "axios";
import {useQuery} from "react-query";

const MyBook = () => {
    const { isLoading, error, data } = useQuery("myPageList", async () => {
        const response = await axiosInstance.get(CONFIG.API_MYBOOK);

        return response.data;
    });
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    if (isLoading) {
        return <div class="loading-1">로딩중</div>;
    }

    if (error) {
        return <div class="error-1">{error.message}</div>;
    }
    if (!isLogined) return <Navigate to="/" replace />; // 로그인 안했다면 메인화면으로 보냄


    return (
        <section class="bg-light py-5">
            <div class="container px-5 my-5">
                <div class="text-center mb-5">
                    <h1 class="fw-bolder">내 책 관리</h1>
                </div>
                <div class="gx-5 justify-content-center">
                    <div class="col-lg-12 col-xl-12">
                        <div class="card mb-5">
                            <div class="card-body p-5">
                                <div class="mb-3">
                                    <span class="text-muted fs-4">나의 기록</span>
                                </div>
                                <table className="table table-hover table-striped">
                                    <thead>
                                    <tr>
                                        <th className="text-align-center">번호</th>
                                        <th>제목</th>
                                        <th className="text-align-center">활동</th>
                                        <th className="text-align-center">등록일</th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                        {data.bookLogList.map((log)=>(
                                            <tr key={log.bookNumber}>
                                                <td className="text-align-center">{log.bookNumber}</td>
                                                <td>{log.bookName}</td>
                                                <td className="text-align-center">{log.bookStatus}</td>
                                                <td className="text-align-center">{log.regDate}</td>
                                                <td></td>
                                                <td></td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                                <div class="d-grid"><a class="btn btn-outline-primary" href="#!">Choose plan</a></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-12 col-xl-12">
                        <div class="card mb-5">
                            <div class="card-body p-5">
                                <div class="mb-3">
                                    <span class="text-muted fs-4">대여중인 책</span>
                                </div>
                                <table className="table table-hover table-striped">
                                    <thead>
                                    <tr>
                                        <th className="text-align-center">번호</th>
                                        <th>제목</th>
                                        <th className="text-align-center">대여일</th>
                                        <th className="text-align-center">반납예정일</th>
                                        <th></th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {data.rentBook.map((book)=>(
                                        <tr key={book.bookId}>
                                            <td className="text-align-center">{book.bookNumber}</td>
                                            <td>{book.bookName}</td>
                                            <td className="text-align-center">{book.bookStatus}</td>
                                            <td></td>
                                            <td></td>
                                            <td><button className="btn btn-outline-primary btn-sm">
                                                반납기한 연장하기
                                            </button></td>
                                            <td><button className="btn btn-outline-success btn-sm">
                                                반납하기
                                            </button></td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                                <div class="d-grid"><a class="btn btn-primary" href="#!">Choose plan</a></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-12 col-xl-12">
                        <div class="card">
                            <div class="card-body p-5">
                                <div class="mb-3">
                                    <span class="text-muted fs-4">추천한 책</span>
                                </div>
                                <table className="table table-hover table-striped">
                                    <thead>
                                    <tr>
                                        <th className="text-align-center">번호</th>
                                        <th>제목</th>
                                        <th className="text-align-center">추천 수</th>
                                        <th className="text-align-center">대여가능여부</th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {data.likeBook.map((book)=>(
                                        <tr key={book.bookId}>
                                            <td className="text-align-center">{book.bookNumber}</td>
                                            <td>{book.bookName}</td>
                                            <td></td>
                                            <td></td>
                                            <td><button className="btn btn-outline-success btn-sm">
                                                추천 취소하기
                                            </button></td>
                                            <td><button className="btn btn-outline-primary btn-sm">
                                                대여하기
                                            </button></td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                                <div class="d-grid"><a class="btn btn-outline-primary" href="#!">Choose plan</a></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default MyBook;
