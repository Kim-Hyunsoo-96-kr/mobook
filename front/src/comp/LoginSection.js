import axios from "axios";

function LoginSection() {
    const CONFIG = {};
    CONFIG.BASE_URL = "http://localhost:8080";
    CONFIG.API_LOGIN = "http://localhost:8080/api/members/login";
    const onSubmit = async (event) => {
        event.preventDefault();

        const form = event.target;

        form.email.value = form.email.value.trim();
        form.password.value = form.password.value.trim();

        if (form.email.value.length == 0) {
            alert("email 입력해주세요.");
            form.email.focus();
            return;
        }

        if (form.password.value.length == 0) {
            alert("password를 입력해주세요.");
            form.password.focus();
            return;
        }

        const email = form.email.value;
        const password = form.password.value;

        try {
            const response = await axios.post(CONFIG.API_LOGIN, {email, password});
            console.log(response);
        }
        catch (e) {
            
        }
    };
    return (
        <section className="py-5">
            <div className="container px-5">
                <div className="bg-light rounded-3 py-5 px-4 px-md-5 mb-5">
                    <div className="text-center mb-5">
                        <div className="feature bg-primary bg-gradient text-white rounded-3 mb-3"><i
                            className="bi bi-envelope"></i></div>
                        <h1 className="fw-bolder">Login</h1>
                        <p className="lead fw-normal text-muted mb-0">We'd love to hear from you</p>
                    </div>
                    <div className="row gx-5 justify-content-center">
                        <div className="col-lg-8 col-xl-6">
                            <form id="contactForm" onSubmit={onSubmit}>
                                <div className="form-floating mb-3">
                                    <input className="form-control" id="email" type="email" name='email'
                                           placeholder="name@example.com"/>
                                    <label htmlFor="email">Email address</label>
                                    <div className="invalid-feedback" data-sb-feedback="email:required">An email is
                                        required.
                                    </div>
                                    <div className="invalid-feedback" data-sb-feedback="email:email">Email is not
                                        valid.
                                    </div>
                                </div>
                                <div className="form-floating mb-3">
                                    <input className="form-control" id="phone" type="tel" placeholder="(123) 456-7890"
                                           name='password'/>
                                    <label htmlFor="password">Password</label>
                                </div>
                                <div className="d-grid">
                                    <button className="btn btn-primary btn-lg" id="submitButton"
                                            type="submit" >Submit
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default LoginSection;
