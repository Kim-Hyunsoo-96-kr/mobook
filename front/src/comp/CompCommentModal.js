import {
    axiosInstance, CONFIG,
    isLoginedSelector,
    loginedUserInfoSelector, queryClient, Toast,
} from "../recoil";
import {useRecoilValue, useSetRecoilState} from "recoil";
import CompComment from "./CompComment";
import Swal from "sweetalert2";
import {useState} from "react";

function CompCommentModal({book}) {
    const [comment, setComment] = useState('');
    const handleInputChange = (event) => {
        setComment(event.target.value);
    };
    const addComment = async (event, bookNumber) => {

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
            setComment('');
            queryClient.invalidateQueries(["bookList"]);
        } catch (e) {
            console.log(e)
        }

    }
    // {`#modal${book.id}`}
    return (
        <p>
            <div style={{marginLeft : '8px', display : 'flex', alignItems : 'center'}} type="button" className="bi bi-chat-dots btn btn-outline-secondary btn-sm" data-bs-toggle="modal"
                 data-bs-target={`#exampleModal${book.bookNumber}`}>
                <div style={{marginLeft : '4px'}}>댓글({book.bookCommentList.length})</div>
            </div>
        <div className="modal fade" id={`exampleModal${book.bookNumber}`}
             tabIndex="-1"
             aria-labelledby={`exampleModal${book.bookNumber}Label`}
             aria-hidden="true">
            <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content">
                <div className="modal-header">
                    <h6 className="modal-title"
                        id={`exampleModal${book.bookNumber}Label`}>댓글({book.bookCommentList.length})</h6>
                    <button type="button"
                            className="btn-close"
                            data-bs-dismiss="modal"
                            aria-label="Close"></button>
                </div>
                <div className="modal-body">
                    <section>
                        <div class="card bg-light">
                            <div class="card-body">
                                {book.bookCommentList.length > 0 ||
                                    <div>
                                        등록된 댓글이 없습니다.
                                    </div>
                                }
                                {book.bookCommentList.map((comment)=>(
                                    <CompComment comment={comment} key={comment.id} />
                                ))}
                            </div>
                        </div>
                    </section>
                </div>
                <form className="modal-footer" onSubmit={(event) => addComment(event, book.bookNumber)}>
                    <textarea
                        className="form-control mb-2"
                        name='comment'
                        rows="3"
                        value={comment}
                        onChange={handleInputChange}
                        placeholder="댓글을 입력해주세요."></textarea>
                    <div class='float-end'>
                        <button type="button"
                                className="btn btn-success"
                                type='submit'>댓글 등록
                        </button>
                    </div>
                </form>
            </div>
            </div>
            </div>
        </p>
    );
}

export default CompCommentModal;
