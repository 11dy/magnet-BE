import React from 'react';
import kakaoImage from '../../asset/logo/kakao.png';
import googleImage from '../../asset/logo/google.png';
import { Link } from 'react-router-dom';

interface LoginButtonProps {
  type: 'Email' | 'Google' | 'Kakao';
}

const LoginButton: React.FC<LoginButtonProps> = ({ type }) => {
  let buttonStyle = '';
  let buttonText = '';
  let buttonImage = '';
  let linkTo = '';

  switch (type) {
    case 'Email':
      buttonStyle = 'bg-blue-500 text-white';
      buttonText = '이메일로 로그인 하기';
      linkTo = '/loginemail';
      break;
    case 'Google':
      buttonStyle = 'bg-slate-100 text-black';
      buttonText = 'Google로 계속하기';
      buttonImage = googleImage;
      // 나중에 리다이렉션 추가해!
      linkTo = '';
      break;
    case 'Kakao':
      buttonStyle = 'bg-yellow-400 text-black';
      buttonText = 'Kakao로 계속하기';
      buttonImage = kakaoImage;
      // 나중에 리다이렉션 추가해!
      linkTo = '';
      break;
    default:
      buttonStyle = 'bg-blue-500 text-white';
      buttonText = '이메일로 로그인 하기';
      linkTo = '/loginemail';
      break;
  }

  return (
    <Link to={linkTo}>
      <div className={`w-80 h-12 flexCenter border-[1px] rounded-xl cursor-pointer ${buttonStyle}`}>
        {buttonImage && <img src={buttonImage} alt={`${type} 이미지`} className="mr-2 w-6 " />}
        <p className="font-semibold">{buttonText}</p>
      </div>
    </Link>
  );
};

export default LoginButton;
