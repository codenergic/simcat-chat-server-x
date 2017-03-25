package org.codenergic.simcat.auth.user.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.codenergic.simcat.data.User;

class HzUser implements Externalizable, Comparable<HzUser> {
	private User user;

	public HzUser() {
		// do nothing
	}

	protected HzUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.write(user.encode());
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		byte[] bs = new byte[in.available()];
		in.readFully(bs);
		user = User.ADAPTER.decode(bs);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HzUser other = (HzUser) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public int compareTo(HzUser o) {
		return user.id.compareTo(o.user.id);
	}
}
