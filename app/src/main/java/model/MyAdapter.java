package model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coolweather.app.R;

import java.util.List;

/**
 * Created by Administrator on 2016/4/30.
 */
public class MyAdapter extends AppBaseAdapter{

    private List<String> mList = null;//
    private Context mContext = null;
    private int resourceID = 0;

    public MyAdapter(Context context, List<String> mList, int resourceID) {
        super(context, mList);
        this.mList = mList;
        this.mContext = context;
        this.resourceID = resourceID;
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if(convertView == null){
//            convertView = LayoutInflater.from(mContext).inflate(resourceID, null, false);
            convertView = super.inflater.inflate(resourceID, null, false);
            TextView textView = (TextView) convertView.findViewById(R.id.item);

            viewHolder = new ViewHolder();
            viewHolder.textView = textView;

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.textView.setText(mList.get(position));

        return convertView;
    }

    class ViewHolder{
        TextView textView = null;

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }
}
