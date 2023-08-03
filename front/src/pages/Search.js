import '../App.css';
import {useRecoilValue, useSetRecoilState} from "recoil";
import {CONFIG, isLoginedSelector, loginedUserInfoAtom, loginedUserInfoSelector} from "../recoil";
import {Link, Navigate} from "react-router-dom";
import axios from "axios";
import {useEffect, useState} from "react";

function Search() {
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    const isLogined = useRecoilValue(isLoginedSelector);
    const [bookData, setBookData] = useState(null)
    const [loading, setLoading] = useState(true)
    useEffect(()=> {
        const getData = async () => {
            try {
                //응답 성공
                const response = await axios.get(CONFIG.API_BOOK_LIST, {
                    headers: {
                        Authorization: `Bearer ${loginedUserInfo.accessToken}`
                    }
                })
                    .then(resp => {
                        setBookData(resp.data)
                    });
            } catch (error) {
                //응답 실패
                console.error(error);
            }
            setLoading(false);
        }
        getData();
    }, []);
    if(!isLogined) return <Navigate to={"/"}/>;
    if(loading) return <div>로딩 중</div>
    return (
        <section className="py-5">
            <div className="container px-5 my-5">
                <main className="mt-5 pt-5">
                    <div className="container-fluid px-4">
                        <h1 className="mt-4">책 목록</h1>

                        <div className="card mb-4">
                            <div className="card-header">
                                <a className="btn btn-primary float-end"
                                   href="register">
                                    <i className="fas fa-edit"></i> 글 작성
                                </a>
                            </div>
                            <div className="card-body">
                                <table className="table table-hover table-striped">
                                    <thead>
                                    <tr>
                                        <th>책 고유번호</th>
                                        <th>제목</th>
                                        <th>좋아요</th>
                                        <th>작성일</th>
                                        <th>대여가능여부</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                        {bookData.bookList.map((book)=>(
                                            <tr key={book.bookId}>
                                                <td>{book.bookNumber}</td>
                                                <td>{book.bookName}</td>
                                                <td>{book.stars}</td>
                                                <td>{book.regDate}</td>
                                                <td>{book.isAble ? "Y" : "N"}</td>
                                                <td><button onClick={() => console.log("Button clicked!")}>
                                                    대여신청
                                                </button></td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </main>
            </div>
        </section>
    );
}

export default Search;
