import {Link, Navigate} from "react-router-dom";
import {useRecoilValue} from "recoil";
import {isLoginedSelector} from "../recoil";

function NavBar(props) {
    const menuList = []
    for(let i = 0; i < props.nav.menu.length; i ++){
        let t = props.nav.menu[i];
        menuList.push(<li class="nav-item" key={i}><Link to={t.router} class="nav-link">{t.title}</Link></li>);
    }
    const isLogined = useRecoilValue(isLoginedSelector);
    return (
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
            <div class="container px-5">
                <a class="navbar-brand" href="index.html">{props.nav.title}</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation"><span class="navbar-toggler-icon"></span></button>
                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
                        {menuList}
                        {isLogined && (
                            <li className="nav-item"><Link to={"/logout"} class="nav-link">로그아웃</Link></li>
                        )}
                        {isLogined || (
                            <li className="nav-item"><Link to={"/login"} class="nav-link">로그인</Link></li>
                        )}
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" id="navbarDropdownBlog" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">마이 페이지</a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdownBlog">
                                <li><a class="dropdown-item" href="blog-home.html">내 정보</a></li>
                                <li><a class="dropdown-item" href="blog-post.html">비밀번호 변경</a></li>
                                <li><a class="dropdown-item" href="blog-post.html">비밀번호 변경</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    );
}

export default NavBar;
