package org.cogroo.tools.checker.rules.util;

import static org.junit.Assert.assertEquals;

import org.cogroo.entities.Token;
import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.entities.impl.TokenCogroo;
import org.cogroo.tools.checker.rules.model.TagMask;
import org.junit.Before;
import org.junit.Test;

public class TagMaskUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testParse1() {
		String s = "number     =   plural gender=female class = finitive-verb";
		TagMask tm = TagMaskUtils.parse(s);
		assertEquals(TagMask.Number.PLURAL, tm.getNumber());
		assertEquals(TagMask.Gender.FEMALE, tm.getGender());
		assertEquals(TagMask.Class.FINITIVE_VERB, tm.getClazz());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParse2() {
		String s = "number =  plura";
		TagMask tm = TagMaskUtils.parse(s);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParse3() {
		String s = "nuber =  plural gender = male";
		TagMask tm = TagMaskUtils.parse(s);
	}

	@Test
	public void testCreateTagMaskFromToken() {
		Token token = new TokenCogroo("dummy", 42);
		MorphologicalTag tag = new MorphologicalTag();
		tag.setGender(TagMask.Gender.FEMALE);
		token.setMorphologicalTag(tag);
		TagMask tm = TagMaskUtils
				.createTagMaskFromToken(token, "gender number");
		assertEquals(token.getMorphologicalTag().getGenderE(), tm.getGender());
	}
}
