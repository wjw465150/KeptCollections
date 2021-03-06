/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.killa.unkempt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.killa.kept.BaseKeptUtil;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.junit.Assert;
import org.junit.Test;

public class TestUnkemptBlockingQueue extends BaseKeptUtil {
    {
	this.parent = "/testunkemptyblockingqueue";
    }

    @Test
    public void testKeptStringQueue() throws Exception {
	final UnkemptBlockingQueue<String> kbq = new UnkemptBlockingQueue<String>(
		String.class, this.keeper, this.parent, Ids.OPEN_ACL_UNSAFE,
		CreateMode.EPHEMERAL);

	Assert.assertNull(kbq.poll(1, TimeUnit.SECONDS));

	final String payload = Long.toString(System.currentTimeMillis());
	kbq.put(payload);

	Assert.assertEquals("not equal", payload, kbq.take());

	final List<String> source = new ArrayList<String>();
	source.add(payload);
	source.add(payload);
	source.add(payload);

	for (final String s : source)
	    kbq.offer(s, Long.MAX_VALUE, TimeUnit.DAYS);

	final List<String> sink = new ArrayList<String>();
	kbq.drainTo(sink);

	Assert.assertEquals("not equal", source, sink);
	Assert.assertEquals("wrong size", 0, kbq.size());
    }
}
