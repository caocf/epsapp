package com.epeisong.data.net.parser;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.logistics.proto.Base.ProtoEQuestion;
import com.epeisong.logistics.proto.Eps.QuestionResp;
import com.epeisong.model.Question;

public class QuestionParser {

	public static Question parse(ProtoEQuestion question) {
		Question q = new Question();
		q.setTitle(question.getTitle());
		q.setContent(question.getContent());
		return q;

	}

	public static List<Question> parse(QuestionResp.Builder resp) {
		List<ProtoEQuestion> list = resp.getQuestionList();
		List<Question> result = new ArrayList<Question>();
		for (ProtoEQuestion item : list) {
			result.add(parse(item));
		}
		return result;
	}

}
