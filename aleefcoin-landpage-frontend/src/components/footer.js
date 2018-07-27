import React from 'react';
import { NavLink, Link } from 'react-router-dom';

export default class Footer extends React.Component {
    render() {
        return (
            <div>
                <footer>
                    <div className="container">
                        <div className="row">
                            <div className="col-md-6 col-lg-6 col-sm-6 foot">
                                <ul>
                                    <li><Link to='/termsofservice' target='_blank' >Terms of Service </Link></li>
                                    <li><Link to='/privacypolicy' target='_blank'>Privacy Policy</Link></li>
                                    <li><Link to='/faq' target='_blank'>FAQ</Link></li>
                                </ul>
                            </div>
                            <div className="col-md-6 col-lg-6 col-sm-6">
                                <p id="copywright" className="footerParg">&copy; ALEEF COIN- All rights reserved 2018</p>
                            </div>
                        </div>
                    </div>
                </footer>
            </div>
        )
    }
}