import '../App.css';
import Header from "../comp/Header";
import {useRecoilValue, useSetRecoilState} from "recoil";
import {axiosInstance, CONFIG, isLoginedSelector, loginedUserInfoAtom, loginedUserInfoSelector} from "../recoil";
import {Navigate} from "react-router-dom";
import axios from "axios";
import {useQuery} from "react-query";

const MyRentBook = () => {
    const { isLoading, error, data } = useQuery("myRentBook", async () => {
        const response = await axiosInstance.get(CONFIG.API_MYRENTBOOK);

        return response.data;
    });
    const isLogined = useRecoilValue(isLoginedSelector); // 로그인 했는지 여부
    if (!isLogined) return <Navigate to="/login" replace />; // 로그인 안했다면 메인화면으로 보냄

    const extendPeriod = async (bookNumber) => {
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_EXTEND_PERIOD}${bookNumber}`);
            alert(response.data.message);
    }
    const returnBook = async (bookNumber) => {
        try{
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_RETURN}${bookNumber}`);
            alert("반납 성공");
        } catch (e) {
            alert("반납이 불가능한 책 입니다.")
        }
    }

    const recommendCancel = async (bookNumber) => {
        try{
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_RECOMMEND_CANCEL}${bookNumber}`);
            alert("추천을 취소했습니다.");
        } catch (e) {
            alert("추천하지 않은 책 입니다.")
        }
    }
    if (isLoading) {
        return <div class="loading-1">로딩중</div>;
    }

    if (error) {
        return <div class="error-1">{error.message}</div>;
    }


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
                                    <span class="text-muted fs-4">대여중인 책</span>
                                </div>
                                <table className="table table-hover table-striped">
                                    <thead>
                                    <tr>
                                        <th className="text-align-center">번호</th>
                                        <th>제목</th>
                                        <th className="text-align-center">추천 수</th>
                                        <th className="text-align-center">대여일</th>
                                        <th className="text-align-center">반납예정일</th>
                                        <th></th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {data.rentBook.map((book)=>(
                                        <tr>
                                            <td className="text-align-center">{book.bookNumber}</td>
                                            <td>{book.bookName}</td>
                                            <td className="text-align-center">{book.recommend}</td>
                                            <td className="text-align-center">{book.rentDate}</td>
                                            <td className="text-align-center">{book.returnDate}</td>
                                            <td><button className="btn btn-outline-primary btn-sm" onClick={() => extendPeriod(book.bookNumber)}>
                                                반납기한 연장하기
                                            </button></td>
                                            <td><button className="btn btn-outline-success btn-sm" onClick={() => returnBook(book.bookNumber)}>
                                                반납하기
                                            </button></td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default MyRentBook;
