import React from 'react';
import ReactDOM, { render } from 'react-dom';
import { BrowserRouter, Switch, Route, Redirect } from 'react-router-dom';

import Login from './src/AleefBoard/Common/login';
import Register from './src/AleefBoard/Common/register';
import ForgetPassword from './src/AleefBoard/Common/forgetPassword';
import ForgotPassword from './src/AleefBoard/Common/forgotPassword';
import TransactionSuccess from './src/AleefBoard/Common/TransactionSuccess';
import ForgetSecretpin from './src/AleefBoard/Common/forgetSecretPin';
import Success from './src/AleefBoard/Common/success';
// import PrivacyPolicy from './src/AleefBoard/Common/privacyPolicy';
// import TermsOfService from './src/AleefBoard/Common/termsofservice';

import AdminDashboard from './src/AleefBoard/Admin/adminDashboard';
import MangeKyc from './src/AleefBoard/Admin/manageKyc';
import AdminTransaction from './src/AleefBoard/Admin/transaction';
import userList from './src/AleefBoard/Admin/userList';
import userPurchaseList from './src/AleefBoard/Admin/userPurchaseList';

import UserDashboard from './src/AleefBoard/User/userDashboard';
import KycDetails from './src/AleefBoard/User/KycDetails';
import UserTransaction from './src/AleefBoard/User/userTransaction';
import purchaseList from './src/AleefBoard/User/purchaseList';
import RefferdMemberList from './src/AleefBoard/User/ReferedPersonList';
import MyProfile from './src/AleefBoard/User/MyProfile';

render(<BrowserRouter>
    <Switch>
        <Redirect exact path="/" to="login" />
        <Route path="/login" component={Login} />
        <Route path="/register" component={Register} />
        <Route path="/forgetpassword" component={ForgetPassword} />
        <Route path="/forgotpassword" component={ForgotPassword} />
        <Route path="/forgetsecretpin" component={ForgetSecretpin} />
        <Route path="/sucess" component={Success}/>
        {/* <Route path="/privacypolicy" component={PrivacyPolicy} /> */}
        {/* <Route path="/termsofservice" component={TermsOfService}/> */}

        <Route path="/admintransaction" component={AdminTransaction} />
        <Route path="/admindashboard" component={AdminDashboard} />
        <Route path="/managekyc" component={MangeKyc} />
        <Route path="/userList" component={userList} />
        <Route path="/userPurchaseList" component={userPurchaseList} />
        <Route path="/transactionsuccess" component={TransactionSuccess} />

        <Route path="/userdashboard" component={UserDashboard} />
        <Route path="/kycdetails" component={KycDetails} />
        <Route path="/usertransaction" component={UserTransaction} />
        <Route path="/purchaseList" component={purchaseList} />
        <Route path="/refferdlist" component={RefferdMemberList} />
        <Route path='/myprofile' component={MyProfile} />
    </Switch>
</BrowserRouter>, document.getElementById('app'));
// render(<KycDetails/>, document.getElementById('app'));
