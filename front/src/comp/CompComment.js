import {useState} from "react";
import {
    axiosInstance,
    CONFIG,
    isLoginedSelector,
    loginedUserInfoAtom,
    loginedUserInfoSelector,
    queryClient,
    Toast,
    Toast2
} from "../recoil";
import {useRecoilValue, useSetRecoilState} from "recoil";
import {Navigate, useNavigate} from "react-router-dom";
import {useQuery} from "react-query";
import Swal from "sweetalert2";

function CompComment({comment}) {
    const [isEditing, setIsEditing] = useState(false);
    const [editedComment, setEditedComment] = useState(comment.comment);
    const isLogined = useRecoilValue(isLoginedSelector);
    const loginedUserInfo = useRecoilValue(loginedUserInfoSelector);
    let isWriter = null
    if(isLogined) {
        isWriter = loginedUserInfo.memberId === comment.memberId
    }
    const handleEditClick = () => {
        setIsEditing(!isEditing); // 토글 기능
    };

    const handleCancelClick = () => {
        setIsEditing(false);
    };
    const editComment = async (event, commentId) => {
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

        try {
            const response = await axiosInstance.post(`${CONFIG.API_BOOK_COMMENT_EDIT}${commentId}`,{comment});
            Toast.fire({
                icon: 'success',
                title: response.data.message
            })
            queryClient.invalidateQueries(["bookList"]);
            setIsEditing(false);

        } catch (e) {
            if (e.response.status == 400)
                Swal.fire(
                    e.response.data.message,
                    '한번 더 확인해주세요.',
                    'warning'
                )
            else
                Swal.fire(
                    '예상치 못한 오류',
                    e.message,
                    'warning'
                )
        }
    }
    const deleteComment = (commentId) => {
        Swal.fire({
            title: '댓글을 삭제하시겠습니까?',
            text: "삭제한 댓글은 돌아오지 않습니다.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: '삭제',
            cancelButtonText: '취소',
            reverseButtons: true, // 버튼 순서 거꾸로
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const response = await axiosInstance.post(`${CONFIG.API_BOOK_COMMENT_DELETE}${commentId}`);
                    Toast.fire({
                        icon: 'success',
                        title: response.data.message
                    })
                    queryClient.invalidateQueries(["bookList"]);

                } catch (e) {
                    if (e.response.status == 400)
                        Swal.fire(
                            e.response.data.message,
                            '한번 더 확인해주세요.',
                            'warning'
                        )
                    else
                        Swal.fire(
                            '예상치 못한 오류',
                            e.message,
                            'warning'
                        )
                }
            }
        })
    }
    return (
        <div className="ms-3 mb-4">
            <div className='d-flex mb-2'>
                <div className="fw-bold">{comment.memberName}</div>
                <div className='ms-3'>{comment.regDate}</div>
                {isWriter ? (
                    <div className='d-flex'>
                        <div className="bi bi-pencil-fill ms-3 cursor" onClick={handleEditClick}/>
                        <div className="bi bi-trash-fill ms-3 cursor" onClick={() => deleteComment(comment.id)}/>
                    </div>
                ):(<div></div>)}
            </div>
            {isEditing ? (
            <form onSubmit={(event) => editComment(event, comment.id)}>
                <textarea className="form-control mb-2" name='comment' value={editedComment} onChange={(e) => setEditedComment(e.target.value)}/>
                <div className='d-flex float-end'>
                    <div className='float-end'>
                        <button type="button" className="btn btn-outline-secondary btn-sm" onClick={handleEditClick}>취소</button>
                    </div>
                    <div>
                        <button type="button"
                                className="btn btn-outline-primary btn-sm ms-1"
                                type='submit'>댓글 수정
                        </button>
                    </div>
                </div>
            </form>
            ) : (
                <div>{comment.comment}</div>
            )}
        </div>
    );
}

export default CompComment;
