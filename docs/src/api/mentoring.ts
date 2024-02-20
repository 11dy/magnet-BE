import axios from 'axios';

const baseUrl = process.env.REACT_APP_BASE_URL || 'NO_BASE_URL';

export type createMentoringData = {
	title: string;
	content: string;
	pay: string;
	period: string;
	participants: number;
	category: string;
};

export const createMentoring = async (data: createMentoringData) => {
	try {
		const authorToken = sessionStorage.getItem('Authorization');
		const refreshToken = sessionStorage.getItem('RefreshToken');
		const response = await axios.post(
			`${baseUrl}/mentoring/create`,
			{
				title: data.title,
				content: data.content,
				pay: data.pay,
				period: data.period,
				participants: data.participants,
				category: data.category,
			},
			{
				headers: {
					Authorization: authorToken,
					RefreshToken: refreshToken,
				},
			},
		);
		return response;
	} catch (error) {
		console.error('멘토링 생성 실패', error);
		throw error;
	}
};

export type getMentoringData = {
	mentoringId: number;
	title: string;
	content: string;
	pay: string;
	period: string;
	participants: number;
	category: string;
	mentorId: number;
	career: string;
	field: string;
	task: string;
	email: string;
	phone: string;
	aboutMe: string;
	github: string;
};

export const getMentoring = async (): Promise<getMentoringData> => {
	try {
		const authorToken = sessionStorage.getItem('Authorization');
		const refreshToken = sessionStorage.getItem('RefreshToken');
		const response = await axios.get<getMentoringData>(`${baseUrl}/mentoring/get`, {
			headers: {
				Authorization: authorToken,
				RefreshToken: refreshToken,
			},
		});
		return response.data;
	} catch (error) {
		console.error('멘토링 조회 실패', error);
		throw error;
	}
};

export type getMentoringListData = {
	content: Content[];
	pageable: Pageable;
	last: boolean;
	totalPages: number;
	totalElements: number;
	size: number;
	number: number;
	sort: Sort;
	first: boolean;
	numberOfElements: number;
	empty: boolean;
};
export type Pageable = {
	pageNumber: number;
	pageSize: number;
	sort: Sort;
	offset: number;
	unpaged: boolean;
	paged: boolean;
};
export type Sort = {
	empty: boolean;
	sorted: boolean;
	unsorted: boolean;
};
export type Content = {
	mentoringId: number;
	title: string;
	content: string;
	pay: string;
	period: string;
	participants: number;
	category: string;
	mentorId: number;
	aboutMe: string;
	field: string;
	task: string;
	mentorName: string;
	career: string;
};

export const getMentoringList = async (
	offset: number,
	size: number,
): Promise<getMentoringListData> => {
	try {
		const response = await axios.get(
			`${baseUrl}/mentoring/listlist?offset=${offset}&size=${size}`,
			{
				headers: {
					'Content-Type': 'application/json',
					'ngrok-skip-browser-warning': 'true',
				},
			},
		);
		return response.data;
	} catch (error) {
		console.error('멘토 리스트 조회 실패', error);
		throw error;
	}
};