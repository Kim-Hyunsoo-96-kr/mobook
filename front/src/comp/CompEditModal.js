import {
    axiosInstance, CONFIG,
    isLoginedSelector,
    loginedUserInfoSelector, queryClient, Toast,
} from "../recoil";
import {useRecoilValue, useSetRecoilState} from "recoil";
import CompComment from "./CompComment";
import Swal from "sweetalert2";
import {useEffect, useState} from "react";

function CompCommentModal({book}) {
    const [bookName, setBookName] = useState(book.bookName);
    const [bookAuthor, setBookAuthor] = useState(book.bookAuthor);
    const [bookPublisher, setBookPublisher] = useState(book.bookPublisher);
    const [bookDescription, setBookDescription] = useState(book.bookDescription);
    const [bookImgUrl, setBookImgUrl] = useState(book.bookImageUrl);
    const bookNameInputChange = (event) => {
        setBookName(event.target.value);
    };
    const bookAuthorInputChange = (event) => {
        setBookAuthor(event.target.value);
    };
    const bookPublisherInputChange = (event) => {
        setBookPublisher(event.target.value);
    };
    const bookDescriptionInputChange = (event) => {
        setBookDescription(event.target.value);
    };
    const edit = async (event, bookNumber) => {

        event.preventDefault();

        const form = event.target;

        form.comment.value = form.comment.value.trim();

        if (form.comment.value.length === 0) {
            Swal.fire(
                '댓글을 입력해주세요.',
                '빈 값 입니다.',
                'warning'
            )
            form.comment.focus();
            return;
        }
        const comment = form.comment.value;

        try{
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_COMMENT}${bookNumber}`, {comment});
            Toast.fire({
                icon: 'success',
                title: response.data.message
            })
            setBookAuthor('');
            queryClient.invalidateQueries(["bookList"]);
        } catch (e) {
            console.log(e)
        }

    }
    useEffect(() => {
        const modalElement = document.getElementById(`editModal${book.bookNumber}`);
        const bookNameInputElement = document.getElementById(`bookNameInput${book.bookNumber}`);

        modalElement.addEventListener('shown.bs.modal', function () {
            bookNameInputElement.focus();
        });

        return () => {
            modalElement.removeEventListener('shown.bs.modal', function () {
                bookNameInputElement.focus();
            });
        }
    }, [book.bookNumber]);
    return (
        <div>
            <div style={{display : 'flex', alignItems : 'center'}} type="button" className="btn btn-outline-success btn-sm" data-bs-toggle="modal"
                 data-bs-target={`#editModal${book.bookNumber}`}>
                <div>책 수정</div>
            </div>
        <div className="modal fade" id={`editModal${book.bookNumber}`}
             tabIndex="-1"
             aria-labelledby={`editModal${book.bookNumber}Label`}
             aria-hidden="true">
            <div className="modal-dialog modal-dialog-centered modal-xl">
            <div className="modal-content">
                <div className="modal-header">
                    <h6 className="modal-title"
                        id={`editModal${book.bookNumber}Label`}>책 수정</h6>
                    <button type="button"
                            className="btn-close"
                            data-bs-dismiss="modal"
                            aria-label="Close"></button>
                </div>
                <div className="modal-body">
                    <section>
                        <div class="card bg-light">
                            <div class="card-body flex-center">
                                <div style={{padding: "5% 0 5% 0"}}>
                                    <img src={book.bookImageUrl} alt="Book Cover" style={{maxWidth : '80%'}} />
                                </div>
                                <div className="margin-left8">
                                    <label className="form-label">제목</label>
                                    <input className="form-control me-2 mb-3" name="bookName" id={`bookNameInput${book.bookNumber}`} value={bookName} onChange={bookNameInputChange}/>
                                    <div>
                                        <button type="button" className="btn btn-sm btn-outline-secondary mb-3" type='submit'>제목으로 검색</button>
                                    </div>
                                    <label className="form-label">저자</label>
                                    <input className="form-control me-2 mb-3" name="bookAuthor" value={bookAuthor} onChange={bookAuthorInputChange}/>
                                    <label className="form-label">출판사</label>
                                    <input className="form-control me-2 mb-3" name="bookPublisher" value={bookPublisher} onChange={bookPublisherInputChange}/>
                                    <label className="form-label">소개</label>
                                    <textarea className="form-control me-2 mb-3 modal-textarea" name="bookDescription" value={bookDescription} onChange={bookDescriptionInputChange}/>
                                </div>
                            </div>
                        </div>
                    </section>
                </div>
                <form className="modal-footer" onSubmit={(event) => edit(event, book.bookNumber)}>
                    <div class='float-end'>
                        <button type="button"
                                className="btn btn-success"
                                type='submit'>수정
                        </button>
                    </div>
                </form>
            </div>
            </div>
            </div>
        </div>
    );
}

export default CompCommentModal;
