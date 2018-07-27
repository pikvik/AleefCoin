import React from 'react';
import Header from './header';
import Footer from './footer';
import { NavLink } from 'react-router-dom';
import SocialShare from './socialShare';

export default class PrivacyPolicy extends React.Component {
    render() {
        return (
            <div>
                <section className="contentSection">
                    <div className="banner">
                        <div className="container">
                            <h2>Privacy Policy</h2>
                            <span className="breadcrumbs"><NavLink to='/landpage'>Home</NavLink>&nbsp; /  &nbsp; Privacy Policy</span>
                        </div>
                    </div>
                    <div className="contentDetailsBg">
                        <div className="container">
                            <p>This privacy policy has been compiled to better serve those who are concerned with how their 	&apos; Personally Identifiable Information &apos; (PII) is being used online. PII, as described in US privacy law and information security, is information that can be used on its own or with other information to identify, contact, or locate a single person, or to identify an individual in context. Please read our privacy policy carefully to get a clear understanding of how we collect, use, protect or otherwise handle your Personally Identifiable Information in accordance with our website. </p>
                            <h3>What personal information do we collect from the people that visit our blog, website or app?</h3>

                            <p>When ordering or registering on our site, as appropriate, you may be asked to enter your name, email address, phone number or other details to help you with your experience.  </p>
                            <h3>When do we collect information?</h3>
                            <p>We collect information from you when you register on our site, subscribe to a newsletter, fill out a form, or enter information on our site. How do we use your information? We may use the information we collect from you when you register, sign up for our newsletter, respond to a survey or marketing communication, surf the website, or use certain other site features in the following ways:  </p>
                            <ul className="contentUl">
                                <li>To administer a contest, promotion, survey or other site feature. </li>
                                <li>To quickly process your transactions.   </li>
                                <li>To send periodic emails regarding your order or other products and services.  </li>
                                <li>To follow up with you after correspondence (live chat, email or phone inquiries. </li>
                            </ul>
                            <h3>How do we protect your information? </h3>
                            <ul className="contentUl">
                                <li>Our website is scanned on a regular basis for security holes and known vulnerabilities in order to make your visit to our site as safe as possible. </li>
                                <li>We use regular Malware Scanning.  </li>
                                <li>Your personal information is contained behind secured networks and is only accessible by a limited number of persons who have special access rights to such systems, and are required to keep the information confidential. In addition, all sensitive/credit information you supply is encrypted via Secure Socket Layer (SSL) technology. </li>
                                <li>We implement a variety of security measures when an investor registers to maintain the safety of your personal information. </li>
                            </ul>
                            <h3>Do we use 	&apos;cookies&apos;?  </h3>
                            <p>Yes. Cookies are small files that a site or its service provider transfers to your computer&apos;s hard drive through your Web browser (if you allow) that enables the site&apos;s or service provider&apos;s systems to recognize your browser and capture and remember certain information. For instance, we use cookies to help us remember and process the items in your shopping cart. They are also used to help us understand your preferences based on previous or current site activity, which enables us to provide you with improved services. We also use cookies to help us compile aggregate data about site traffic and site interaction so that we can offer better site experiences and tools in the future. </p>
                            <h3>We use cookies to: </h3>
                            <ul className="contentUl">
                                <li>Help remember and process the items in the shopping cart.  </li>
                                <li>Understand and save user’s preferences for future visits. </li>
                                <li>Keep track of advertisements. </li>
                                <li>Compile aggregate data about site traffic and site interactions in order to offer better site experiences and tools in the future. We may also use trusted third-party services that track this information on our behalf. </li>
                            </ul>
                            <p>You can choose to have your computer warn you each time a cookie is being sent, or you can choose to turn off all cookies. You do this through your browser settings. Since browser is a little different, look at your browser&apos;s Help Menu to learn the correct way to modify your cookies.  </p>
                            <p>If you turn cookies off, some of the features that make your site experience more efficient may not function properly. It won&apos;t affect the user&apos;s experience that make your site experience more efficient and may not function properly.  </p>
                            <h3>Third-party disclosure  </h3>
                            <p>We do not sell, trade, or otherwise transfer to outside parties your Personally Identifiable Information unless we provide users with advance notice. This does not include website hosting partners and other parties who assist us in operating our website, conducting our business, or serving our users, so long as those parties agree to keep this information confidential. We may also release information when its release is appropriate to comply with the law, enforce our site policies, or protect ours or others rights, property or safety.</p>
                            <p>However, non-personally identifiable visitor information may be provided to other parties for marketing, advertising, or other uses. </p>
                            <h3>Third-party links  </h3>
                            <p>We do not include or offer third-party products or services on our website. </p>
                            <h3>We have implemented the following:  </h3>
                            <ul className="contentUl">
                                <li>Remarketing with Google AdWords </li>
                                <li>Google Analytics Reporting</li>
                            </ul>
                            <h3>We agree to the following:</h3>
                            <p>Users can visit our site anonymously. </p>
                            <p>Once this privacy policy is created, we will add a link to it on our homepage or as a minimum, on the first significant page after entering our website.  </p>
                            <p>Our Privacy Policy link includes the word &apos;Privacy&apos; and can easily be found on the page specified above.</p>
                            <p>You will be notified of any Privacy Policy changes:  </p>
                            <ul className="contentUl">
                                <li> Via email </li>
                            </ul>
                            <p>You can change your personal information:  </p>
                            <ul className="contentUl">
                                <li> By emailing us  </li>
                            </ul>
                            <h3>We do not specifically market to children under the age of 13 years old.  </h3>
                            <h3>Fair Information Practices</h3>
                            <p>The Fair Information Practices Principles form the backbone of privacy law in the United States and the concepts they include have played a significant role in the development of data protection laws around the globe. Understanding the Fair Information Practice Principles and how they should be implemented is critical to comply with the various privacy laws that protect personal information. </p>
                            <p>In order to be in line with Fair Information Practices we will take the following responsive action, should a data breach occur: </p>
                            <p>We will notify you via email  </p>
                            <ul className="contentUl">
                                <li> Other  </li>
                            </ul>
                            <p>Within 30 business days we also agree to the Individual Redress Principle which requires that individuals have the right to legally pursue enforceable rights against data collectors and processors who fail to adhere to the law. This principle requires not only that individuals have enforceable rights against data users, but also that individuals have recourse to courts or government agencies to investigate and/or prosecute noncompliance by data processors.  </p>
                            <p></p>
                        </div>
                    </div>
                </section>
                <SocialShare />
                <Footer />
            </div>
        )
    }
}